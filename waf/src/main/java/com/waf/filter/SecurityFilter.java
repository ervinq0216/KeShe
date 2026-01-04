package com.waf.filter;

import com.waf.service.DdosService;
import com.waf.service.LogService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(1) // 优先级最高，最先执行
public class SecurityFilter implements Filter {

    @Autowired
    private LogService logService;

    @Autowired
    private DdosService ddosService; // 注入 DDoS 检测服务

    // --- 规则定义区域 ---

    // 1. SQL 注入正则
    private static final String SQL_REGEX = "(?i)(.*)(\\b)+(OR|SELECT|INSERT|DELETE|UPDATE|DROP|UNION)(\\b)+(.*)";
    private static final Pattern SQL_PATTERN = Pattern.compile(SQL_REGEX);

    // 2. XSS 跨站脚本攻击正则
    private static final String XSS_REGEX = "(?i)(<script|javascript:|onload=|onerror=|onclick=|onmouseover=|<iframe>|<img>|<body>|<style>)";
    private static final Pattern XSS_PATTERN = Pattern.compile(XSS_REGEX);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        String uri = req.getRequestURI();
        String method = req.getMethod();

        // --- 0. 最优先检测：DDoS 防御 ---
        // 如果判定为 DDoS，直接在这里掐断，不进行后续的 RequestWrapper 包装和正则运算，节省 CPU
        if (ddosService.isDdosAttack(ip)) {
            log.warn("!! [DDoS] 流量异常，IP {} 已被临时封禁", ip);

            res.setStatus(429); // 429 Too Many Requests
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"code\": 429, \"message\": \"访问过于频繁，IP已被暂时封禁!\"}");
            return;
        }

        // 排除掉查询日志的接口，防止自己拦截自己展示的数据
        if (uri.contains("/api/logs")) {
            chain.doFilter(req, res); // 这里不需要 wrapper，直接放行原请求即可
            return;
        }

        // --- 包装请求 (为了能读取 Body) ---
        RequestWrapper requestWrapper = null;
        if (req instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper(req);
        }

        log.info(">> [流量监控] 来自IP: {} 访问了: {}", ip, uri);

        // --- 检测点 A：URL 参数 (SQL + XSS) ---
        Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            for (String value : entry.getValue()) {
                // 检查 SQL
                if (isSqlInjection(value)) {
                    blockRequest(res, ip, uri, method, "SQL注入(URL参数)", value);
                    return;
                }
                // 检查 XSS
                if (isXssAttack(value)) {
                    blockRequest(res, ip, uri, method, "XSS攻击(URL参数)", value);
                    return;
                }
            }
        }

        // --- 检测点 B：Body 内容 (POST/PUT) (SQL + XSS) ---
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            String body = requestWrapper.getBodyString();
            // 检查 SQL
            if (isSqlInjection(body)) {
                blockRequest(res, ip, uri, method, "SQL注入(Body内容)", body);
                return;
            }
            // 检查 XSS
            if (isXssAttack(body)) {
                blockRequest(res, ip, uri, method, "XSS攻击(Body内容)", body);
                return;
            }
        }

        // 所有检查通过，放行 (注意要传 wrapper)
        chain.doFilter(requestWrapper, response);
    }

    /**
     * 统一拦截处理 + 记录日志
     */
    private void blockRequest(HttpServletResponse res, String ip, String uri, String method, String type, String payload) throws IOException {
        log.warn("!! [危险] 拦截到攻击! IP: {}, 类型: {}, Payload: {}", ip, type, payload);

        // 将攻击记录写入数据库
        try {
            logService.saveAttackLog(ip, uri, method, type, payload);
        } catch (Exception e) {
            log.error("日志写入失败: ", e);
        }

        res.setStatus(403);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"code\": 403, \"message\": \"系统检测到非法内容(" + type + ")，请求被拒绝!\"}");
    }

    // SQL 检测逻辑
    private boolean isSqlInjection(String value) {
        if (value == null) return false;
        return SQL_PATTERN.matcher(value).find();
    }

    // XSS 检测逻辑
    private boolean isXssAttack(String value) {
        if (value == null) return false;
        return XSS_PATTERN.matcher(value).find();
    }
}