package com.aiquiz.aiquizs.model.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/teacher
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}