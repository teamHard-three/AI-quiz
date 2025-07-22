package com.aiquiz.aiquizs.mapper;

import com.aiquiz.aiquizs.model.entity.Feedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper

/**
 * 反馈表数据访问层
 */
public interface FeedbackMapper extends BaseMapper<Feedback> {
}