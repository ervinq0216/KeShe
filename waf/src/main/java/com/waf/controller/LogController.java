package com.waf.controller;

import com.waf.entity.AttackLog;
import com.waf.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*") // 关键！允许前端跨域访问 (前后端分离必备)
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    public List<AttackLog> getLogs() {
        return logService.getAllLogs();
    }
}