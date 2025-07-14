package com.aiquiz.aiquizs.service;

import com.aiquiz.aiquizs.model.entity.User;
import com.aiquiz.aiquizs.model.vo.LoginUserVO;
import com.aiquiz.aiquizs.model.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String checkPassword,String userRole);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    List<User> getAllTeachers();
}
