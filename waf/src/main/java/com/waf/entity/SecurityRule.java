package com.waf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("security_rules")
public class SecurityRule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ruleName;
    private String regexPattern; // 核心：正则表达式
    private String ruleType;     // SQL 或 XSS
    private Integer isActive;    // 1=启用, 0=禁用
}