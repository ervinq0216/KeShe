package com.waf.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper; // 记得导入
import com.waf.entity.AttackLog;
import com.waf.mapper.AttackLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List; // 记得导入

@Service
public class LogService {

    @Autowired
    private AttackLogMapper attackLogMapper;

    // ... 原有的 saveAttackLog 方法保持不变 ...
    public void saveAttackLog(String ip, String uri, String method, String type, String payload) {
        // ... (保持原样)
        AttackLog log = new AttackLog();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(method);
        log.setAttackType(type);
        if (payload != null && payload.length() > 2000) {
            payload = payload.substring(0, 2000) + "...";
        }
        log.setPayload(payload);
        log.setCreateTime(LocalDateTime.now());
        attackLogMapper.insert(log);
    }

    /**
     * NEW: 获取所有攻击日志，按时间倒序排列
     */
    public List<AttackLog> getAllLogs() {
        QueryWrapper<AttackLog> query = new QueryWrapper<>();
        query.orderByDesc("create_time"); // 最新发生的攻击排在最前面
        return attackLogMapper.selectList(query);
    }
}