package com.aiquiz.aiquizs.config;

import com.aiquiz.aiquizs.model.vo.LoginUserVO;
import com.aiquiz.aiquizs.model.vo.UserVO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class jwt {
    private static String signKey = "SVRIRUlNQQ=="; //自己制定的密钥  base64编码结果格式
    private static Long expire = 86400000L;

    /**
     * 生成JWT令牌
     *
     * @return
     */
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)//传入自定义数据
                .signWith(SignatureAlgorithm.HS256, signKey)//设置编码格式，以及密钥
                .setExpiration(new Date(System.currentTimeMillis() + expire))//设置过期时间 expire ms为单位
                .compact();//生成令牌
        return jwt;
    }

    /**
     * 解析JWT令牌
     *
     * @param jwt
     * @return
     */
    public static UserVO parseJwt(String jwt) {
        Map<String, Object> claims = Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();

        UserVO UserVO = new UserVO();
        UserVO.setId(Long.valueOf(claims.get("userId").toString()));
        UserVO.setUserAccount(claims.get("userAccount").toString());
        UserVO.setUserRole(claims.get("userRole").toString());
        return UserVO;
    }
}
