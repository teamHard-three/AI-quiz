package com.aiquiz.aiquizs.Interceptors;

import com.aiquiz.aiquizs.config.jwt;
import com.aiquiz.aiquizs.model.vo.UserVO;
import com.aiquiz.aiquizs.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class Interceptor implements HandlerInterceptor {
    @Override//返回True放行 返回false拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = (String) request.getSession().getAttribute("Authorization");
//        if (token == null || token.isEmpty()) {
//            response.setStatus(401);
//            return false; // 拦截请求
//        }
        try {
//            // 验证token
//            UserVO userVO = jwt.parseJwt(token);
            UserVO userVO=new UserVO();
            userVO.setId(1943605749217304578L); // 模拟用户ID
            userVO.setUserRole("student");
            UserHolder.saveUser(userVO); // 将用户信息保存到线程本地变量中
            return true; // 放行请求
        } catch (Exception e) {
            response.setStatus(401); // 验证失败，返回401状态码
            return false; // 拦截请求
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}