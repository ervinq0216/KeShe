package com.waf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attack_logs")
public class AttackLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ipAddress;
    private String requestUri;
    private String requestMethod;
    private String attackType; // 例如：SQL注入
    private String payload;    // 攻击的具体内容
    private LocalDateTime createTime;
}