package com.waf.controller;

import com.waf.entity.SecurityRule;
import com.waf.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @GetMapping
    public List<SecurityRule> list() {
        return ruleService.getAllRules();
    }

    @PostMapping
    public String add(@RequestBody SecurityRule rule) {
        ruleService.addRule(rule);
        return "新增成功";
    }

    @PutMapping
    public String update(@RequestBody SecurityRule rule) {
        ruleService.updateRule(rule);
        return "更新成功";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        ruleService.deleteRule(id);
        return "删除成功";
    }
}