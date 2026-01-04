package com.waf.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.waf.entity.SecurityRule;
import com.waf.mapper.SecurityRuleMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class RuleService {

    @Autowired
    private SecurityRuleMapper ruleMapper;

    // 本地缓存：避免每次请求都查数据库，提升性能
    private List<Pattern> sqlPatterns = new ArrayList<>();
    private List<Pattern> xssPatterns = new ArrayList<>();

    /**
     * 初始化时加载规则
     */
    @PostConstruct
    public void init() {
        refreshRules();
    }

    /**
     * 刷新规则缓存 (当在前端增删改规则后调用)
     */
    public synchronized void refreshRules() {
        sqlPatterns.clear();
        xssPatterns.clear();

        // 只查询启用的规则
        QueryWrapper<SecurityRule> query = new QueryWrapper<>();
        query.eq("is_active", 1);
        List<SecurityRule> rules = ruleMapper.selectList(query);

        for (SecurityRule rule : rules) {
            try {
                // 预编译正则表达式
                Pattern p = Pattern.compile(rule.getRegexPattern(), Pattern.CASE_INSENSITIVE);
                if ("SQL".equalsIgnoreCase(rule.getRuleType())) {
                    sqlPatterns.add(p);
                } else if ("XSS".equalsIgnoreCase(rule.getRuleType())) {
                    xssPatterns.add(p);
                }
            } catch (Exception e) {
                System.err.println("规则编译失败: " + rule.getRuleName());
            }
        }
        System.out.println("规则加载完成：SQL规则 " + sqlPatterns.size() + " 条，XSS规则 " + xssPatterns.size() + " 条");
    }

    public List<Pattern> getSqlPatterns() { return sqlPatterns; }
    public List<Pattern> getXssPatterns() { return xssPatterns; }

    // 提供给 Controller 用的基础增删改查
    public List<SecurityRule> getAllRules() { return ruleMapper.selectList(null); }
    public void addRule(SecurityRule rule) { ruleMapper.insert(rule); refreshRules(); } // 插入后自动刷新缓存
    public void updateRule(SecurityRule rule) { ruleMapper.updateById(rule); refreshRules(); }
    public void deleteRule(Integer id) { ruleMapper.deleteById(id); refreshRules(); }
}