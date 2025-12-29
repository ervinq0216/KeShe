package com.waf.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(1) // 设置优先级，1为最高优先级，最先执行
public class SecurityFilter implements Filter {

    // 定义一个简单的 SQL 注入正则 (匹配 ' or 1=1 这种常见攻击)
    // (?i) 表示忽略大小写
    private static final String SQL_REGEX = "(?i)(.*)(\\b)+(OR|SELECT|INSERT|DELETE|UPDATE|DROP|UNION)(\\b)+(.*)";
    private static final Pattern SQL_PATTERN = Pattern.compile(SQL_REGEX);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        String uri = req.getRequestURI();

        log.info(">> [流量监控] 来自IP: {} 访问了: {}", ip, uri);

        // 1. 获取所有请求参数 (暂时只检测 URL 参数，Body参数后续再加)
        Map<String, String[]> parameterMap = req.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            for (String value : values) {
                // 2. 检测 SQL 注入
                if (isSqlInjection(value)) {
                    log.warn("!! [危险] 检测到 SQL 注入攻击! IP: {}, 参数: {}", ip, value);

                    // 3. 拦截处理：设置状态码并返回错误信息
                    res.setStatus(403);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"code\": 403, \"message\": \"系统检测到非法字符，请求被拒绝!\"}");

                    // 核心：直接 return，不再执行 chain.doFilter，请求到此为止
                    return;
                }
            }
        }

        // TODO: 这里留给未来接入 AI DDoS 检测模块
        // boolean isDDoS = aiService.check(ip);

        // 4. 如果安全，放行，进入 Controller
        chain.doFilter(request, response);
    }

    /**
     * 简单的正则匹配检测
     */
    private boolean isSqlInjection(String value) {
        if (value == null) return false;
        return SQL_PATTERN.matcher(value).find();
    }
}