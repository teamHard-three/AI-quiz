package com.aiquiz.aiquizs.service.impl;

import com.aiquiz.aiquizs.mapper.FeedbackMapper;
import com.aiquiz.aiquizs.model.entity.Feedback;
import com.aiquiz.aiquizs.service.FeedbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 反馈服务实现类
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
}