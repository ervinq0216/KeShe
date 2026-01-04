package com.waf.service;

import com.waf.entity.AttackLog;
import com.waf.mapper.AttackLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    @Autowired
    private AttackLogMapper attackLogMapper;

    /**
     * 保存攻击日志到数据库
     */
    public void saveAttackLog(String ip, String uri, String method, String type, String payload) {
        AttackLog log = new AttackLog();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(method);
        log.setAttackType(type);

        // 截取过长的 payload 防止数据库报错 (假设字段限制是 TEXT，其实很大，但以防万一)
        if (payload != null && payload.length() > 2000) {
            payload = payload.substring(0, 2000) + "...";
        }
        log.setPayload(payload);
        log.setCreateTime(LocalDateTime.now());

        attackLogMapper.insert(log);
    }
}