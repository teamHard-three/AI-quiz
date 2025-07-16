package com.aiquiz.aiquizs.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 **/
@Data
public class UserVO implements Serializable {
    /**
     * 用户 id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户角色
     */
    private String userRole;

}
