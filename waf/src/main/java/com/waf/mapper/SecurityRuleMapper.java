package com.waf.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.waf.entity.SecurityRule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SecurityRuleMapper extends BaseMapper<SecurityRule> {
}