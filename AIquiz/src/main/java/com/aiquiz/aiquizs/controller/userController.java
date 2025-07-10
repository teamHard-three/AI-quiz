package com.aiquiz.aiquizs.controller;

import com.aiquiz.aiquizs.commom.BaseResponse;
import com.aiquiz.aiquizs.commom.ErrorCode;
import com.aiquiz.aiquizs.commom.ResultUtils;
import com.aiquiz.aiquizs.exception.BusinessException;
import com.aiquiz.aiquizs.model.dto.user.UserLoginRequest;
import com.aiquiz.aiquizs.model.dto.user.UserRegisterRequest;
import com.aiquiz.aiquizs.model.entity.User;
import com.aiquiz.aiquizs.model.vo.LoginUserVO;
import com.aiquiz.aiquizs.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class userController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public BaseResponse<String> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return ResultUtils.error(400, "请求体不能为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userRole = userRegisterRequest.getUserRole();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,userRole)) {
            return null;
        }
        long result=-1;
         result = userService.userRegister(userAccount, userPassword, checkPassword,userRole);
        if (result==-1)
            return ResultUtils.error(500, "注册失败，请稍后再试");
        return ResultUtils.success("注册成功");
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }
    @PostMapping("/logout")
    public BaseResponse<String> userLogout(HttpServletRequest request) {
        // 清除用户登录状态
        request.getSession().removeAttribute("Authorization");
        return ResultUtils.success("退出成功");
    }
}
