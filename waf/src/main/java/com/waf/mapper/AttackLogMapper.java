package com.waf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.waf.entity.AttackLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttackLogMapper extends BaseMapper<AttackLog> {
}