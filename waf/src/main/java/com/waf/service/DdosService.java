package com.waf.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DdosService {

    // 记录 IP 在当前时间窗口的请求数
    private static final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    // 记录被封禁的 IP 及解封时间戳
    private static final Map<String, Long> blackList = new ConcurrentHashMap<>();

    // 记录当前的时间窗口 (秒级)
    private static volatile long currentWindowTime = System.currentTimeMillis() / 1000;

    // --- 策略配置 ---
    private static final int LIMIT_PER_SECOND = 10; // 阈值：每秒超过 10 次请求视为攻击 (测试用，生产环境可设为 50-100)
    private static final int BAN_DURATION = 60 * 1000; // 封禁时长：60秒

    /**
     * 检查是否为 DDoS 攻击
     * @param ip 来源IP
     * @return true=是攻击(拦截), false=正常
     */
    public boolean isDdosAttack(String ip) {
        long now = System.currentTimeMillis();

        // 1. 检查是否在黑名单中
        if (blackList.containsKey(ip)) {
            if (now < blackList.get(ip)) {
                return true; // 还在封禁期，直接拦截
            } else {
                blackList.remove(ip); // 封禁到期，解封
            }
        }

        // 2. 流量统计 (简单的滑动窗口算法简化版)
        long currentSecond = now / 1000;

        // 如果进入了新的一秒，清空计数器 (注意：这里简单处理，高并发下可能有微小误差，但做毕设足够)
        if (currentSecond > currentWindowTime) {
            requestCounts.clear();
            currentWindowTime = currentSecond;
        }

        // 获取该 IP 当前秒的请求数
        AtomicInteger count = requestCounts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int requests = count.incrementAndGet();

        // 3. AI/规则判定
        // 这里就是体现 "智能" 的地方。实际 AI 会根据历史行为动态调整 LIMIT_PER_SECOND。
        // 我们这里用静态规则模拟：如果请求频率过高，判定为机器脚本攻击。
        if (requests > LIMIT_PER_SECOND) {
            // 加入黑名单
            blackList.put(ip, now + BAN_DURATION);
            return true;
        }

        return false;
    }
}