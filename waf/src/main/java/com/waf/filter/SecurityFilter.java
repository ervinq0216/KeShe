package com.waf.filter;

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
@Order(1)
public class SecurityFilter implements Filter {

    // 注入日志服务
    @Autowired
    private LogService logService; // NEW: 这里注入了我们刚才写的 Service

    private static final String SQL_REGEX = "(?i)(.*)(\\b)+(OR|SELECT|INSERT|DELETE|UPDATE|DROP|UNION)(\\b)+(.*)";
    private static final Pattern SQL_PATTERN = Pattern.compile(SQL_REGEX);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        RequestWrapper requestWrapper = null;
        if (req instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper(req);
        }

        String ip = req.getRemoteAddr();
        String uri = req.getRequestURI();
        String method = req.getMethod();

        log.info(">> [流量监控] 来自IP: {} 访问了: {}", ip, uri);

        // --- 检测点 A：URL 参数 ---
        Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            for (String value : entry.getValue()) {
                if (isSqlInjection(value)) {
                    // NEW: 拦截时传入更多信息
                    blockRequest(res, ip, uri, method, "SQL注入(URL参数)", value);
                    return;
                }
            }
        }

        // --- 检测点 B：Body 内容 ---
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            String body = requestWrapper.getBodyString();
            if (isSqlInjection(body)) {
                // NEW: 拦截时传入更多信息
                blockRequest(res, ip, uri, method, "SQL注入(Body内容)", body);
                return;
            }
        }

        chain.doFilter(requestWrapper, response);
    }

    /**
     * 拦截处理 + 记录日志
     */
    private void blockRequest(HttpServletResponse res, String ip, String uri, String method, String type, String payload) throws IOException {
        log.warn("!! [危险] 拦截到攻击! IP: {}, 类型: {}", ip, type);

        // NEW: 将攻击记录写入数据库
        try {
            logService.saveAttackLog(ip, uri, method, type, payload);
        } catch (Exception e) {
            log.error("日志写入失败: ", e); // 防止数据库挂了影响拦截返回
        }

        res.setStatus(403);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"code\": 403, \"message\": \"系统检测到非法字符，请求被拒绝!\"}");
    }

    private boolean isSqlInjection(String value) {
        if (value == null) return false;
        return SQL_PATTERN.matcher(value).find();
    }
}