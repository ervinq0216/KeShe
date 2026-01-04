package com.waf.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 请求包装器
 * 作用：将 Request 的 Body 内容缓存起来，允许被多次读取
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 在构造时就读取流，并保存到 body 字节数组中
        this.body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    /**
     * 重写 getInputStream，每次被调用都返回一个新的流（来源于缓存的 body）
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    //不仅要重写流，还要提供一个方便获取 String 内容的方法给 Filter 用
    public String getBodyString() {
        return new String(this.body, StandardCharsets.UTF_8);
    }
}