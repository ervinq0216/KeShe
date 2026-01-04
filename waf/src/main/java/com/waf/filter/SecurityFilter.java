package com.waf.filter;

import com.waf.service.DdosService;
import com.waf.service.LogService;
import com.waf.service.RuleService; // 1. 引入规则服务
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(1) // 优先级最高，最先执行
public class SecurityFilter implements Filter {

    @Autowired
    private LogService logService;

    @Autowired
    private DdosService ddosService;

    @Autowired
    private RuleService ruleService; // 2. 注入动态规则服务

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        String uri = req.getRequestURI();
        String method = req.getMethod();

        // --- 0. 最优先检测：DDoS 防御 ---
        if (ddosService.isDdosAttack(ip)) {
            log.warn("!! [DDoS] 流量异常，IP {} 已被临时封禁", ip);

            // 【重要修复】拦截时必须手动写入日志，否则前端看不到记录
            try {
                logService.saveAttackLog(ip, uri, method, "DDoS攻击", "检测到高频访问，触发临时封禁策略");
            } catch (Exception e) {
                log.error("DDoS日志写入失败: ", e);
            }

            res.setStatus(429); // 429 Too Many Requests
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"code\": 429, \"message\": \"访问过于频繁，IP已被暂时封禁!\"}");
            return;
        }

        // 排除掉管理接口（日志查询、规则管理），防止自己拦截自己
        if (uri.contains("/api/logs") || uri.contains("/api/rules")) {
            chain.doFilter(req, res); // 不需要 wrapper，直接放行
            return;
        }

        // --- 包装请求 (为了能多次读取 Body) ---
        RequestWrapper requestWrapper = null;
        if (req instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper(req);
        }

        // 仅打印业务请求日志 (过滤掉静态资源，让控制台更干净)
        if (!uri.endsWith(".js") && !uri.endsWith(".css") && !uri.endsWith(".png")) {
            log.info(">> [流量监控] 来自IP: {} 访问了: {}", ip, uri);
        }

        // --- 检测点 A：URL 参数 (动态规则: SQL + XSS) ---
        Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            for (String value : entry.getValue()) {
                if (checkRules(value, "SQL")) {
                    blockRequest(res, ip, uri, method, "SQL注入(URL参数)", value);
                    return;
                }
                if (checkRules(value, "XSS")) {
                    blockRequest(res, ip, uri, method, "XSS攻击(URL参数)", value);
                    return;
                }
            }
        }

        // --- 检测点 B：Body 内容 (动态规则: SQL + XSS) ---
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            String body = requestWrapper.getBodyString();
            if (checkRules(body, "SQL")) {
                blockRequest(res, ip, uri, method, "SQL注入(Body内容)", body);
                return;
            }
            if (checkRules(body, "XSS")) {
                blockRequest(res, ip, uri, method, "XSS攻击(Body内容)", body);
                return;
            }
        }

        // 所有检查通过，放行 (注意传递 requestWrapper)
        chain.doFilter(requestWrapper, response);
    }

    /**
     * 核心：统一动态规则匹配逻辑
     * @param value 待检测的字符串
     * @param type 规则类型 (SQL / XSS)
     */
    private boolean checkRules(String value, String type) {
        if (value == null) return false;

        List<Pattern> patterns;
        // 根据类型从 Service 获取缓存的正则列表
        if ("SQL".equals(type)) {
            patterns = ruleService.getSqlPatterns();
        } else {
            patterns = ruleService.getXssPatterns();
        }

        if (patterns == null) return false;

        // 遍历所有启用状态的正则规则
        for (Pattern p : patterns) {
            if (p.matcher(value).find()) {
                return true; // 只要命中任何一条规则，直接判定为攻击
            }
        }
        return false;
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
}