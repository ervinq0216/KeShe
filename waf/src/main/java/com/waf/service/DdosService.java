package com.waf.service;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DdosService {

    // 记录 IP 在当前时间窗口的请求数
    private static final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    // 记录被封禁的 IP 及解封时间戳
    private static final Map<String, Long> blackList = new ConcurrentHashMap<>();

    // 流量历史记录 (用于 AI 学习基线)，存储最近 20 秒的总请求数
    private static final Queue<Integer> trafficHistory = new LinkedList<>();
    // 历史样本最大长度
    private static final int HISTORY_SIZE = 20;

    // 记录当前的时间窗口 (秒级)
    private static volatile long currentWindowTime = System.currentTimeMillis() / 1000;

    // 当前秒的总请求数 (所有 IP 加在一起)
    private static final AtomicInteger totalRequestsPerSecond = new AtomicInteger(0);

    // 封禁时长：10秒
    private static final int BAN_DURATION = 10 * 1000;

    // 最小基准阈值 (防止系统刚启动时没数据，阈值过低导致误杀)
    private static final int MIN_LIMIT = 5;

    /**
     * 检查是否为 DDoS 攻击 (集成动态基线算法)
     * @param ip 来源IP
     * @return true=是攻击(拦截), false=正常
     */
    public boolean isDdosAttack(String ip) {
        long now = System.currentTimeMillis();

        // 1. 检查黑名单
        if (blackList.containsKey(ip)) {
            if (now < blackList.get(ip)) {
                return true;
            } else {
                blackList.remove(ip); // 解封
            }
        }

        // 2. 时间窗口轮转 & 历史数据采样
        long currentSecond = now / 1000;
        synchronized (this) {
            if (currentSecond > currentWindowTime) {
                // 时间过了1秒，把上一秒的总流量存入历史记录
                if (trafficHistory.size() >= HISTORY_SIZE) {
                    trafficHistory.poll(); // 移除最老的数据
                }
                trafficHistory.offer(totalRequestsPerSecond.get()); // 存入上一秒数据

                // 重置计数器
                requestCounts.clear();
                totalRequestsPerSecond.set(0);
                currentWindowTime = currentSecond;
            }
        }

        // 3. 计数
        totalRequestsPerSecond.incrementAndGet(); // 全局流量+1
        AtomicInteger count = requestCounts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int requests = count.incrementAndGet();   // 单IP流量+1

        // 4. 【AI 核心】计算动态阈值
        int dynamicLimit = calculateDynamicLimit();

        // 5. 判定
        if (requests > dynamicLimit) {
            // 加入黑名单
            blackList.put(ip, now + BAN_DURATION);
            System.out.println("!! 触发AI防御: 当前IP请求 " + requests + " > 动态阈值 " + dynamicLimit);
            return true;
        }

        return false;
    }

    /**
     * AI 算法：基于 3-Sigma 原则计算异常阈值
     * 阈值 = 平均值 + 3 * 标准差
     */
    private int calculateDynamicLimit() {
        if (trafficHistory.isEmpty()) {
            return MIN_LIMIT; // 还没历史数据，使用保底阈值
        }

        // 1. 计算平均值 (Mean)
        double sum = 0;
        for (int flow : trafficHistory) {
            sum += flow;
        }
        double mean = sum / trafficHistory.size();

        // 2. 如果平时流量很小（比如没人在用），直接返回一个较大的保底值，防止误杀自己
        if (mean < 2) {
            return MIN_LIMIT;
        }

        // 3. 计算标准差 (Standard Deviation)
        double varianceSum = 0;
        for (int flow : trafficHistory) {
            varianceSum += Math.pow(flow - mean, 2);
        }
        double stdDev = Math.sqrt(varianceSum / trafficHistory.size());

        // 4. 3-Sigma 准则：超过 "均值+3倍标准差" 视为统计学上的极小概率异常事件
        // 这里的系数 3.0 可以调整，系数越小越敏感，越大越宽松
        int limit = (int) (mean + 3 * stdDev);

        // 确保动态阈值不低于保底值
        return Math.max(limit, MIN_LIMIT);
    }
}