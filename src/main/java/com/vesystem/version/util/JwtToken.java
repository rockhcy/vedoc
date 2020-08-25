package com.vesystem.version.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther hcy
 * @create 2020-08-24 15:09
 * @Description
 */
public class JwtToken {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    /**
     * JWT签名密钥
     */
    public static final String JWT_SECRET_KEY = "hcy";

    /**
     * 角色的key
     **/
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_ROLE_ID = "roleId";
    public static final String KEY_USER_ID = "userId";

    /**
     * REFRESH_TIME 令牌刷新时间
     * 30分钟
     */
    public static final long REFRESH_TIME = 1800L;
    /**
     * 令牌过期时间
     * 60分钟
     */
    public static final long EXPIRATION = REFRESH_TIME << 1;

    /**
     * 构建令牌
     * @param username
     * @param roles
     * @param userName
     * @param userId
     * @return
     */
    public static String createToken(String username, List<String> roles,String userName,Integer userId) {
        Map<String,Object> map = new HashMap<>(4);
        map.put(KEY_USER_NAME,userName);
        map.put(KEY_ROLE_ID, String.join(",", roles));
        map.put(KEY_USER_ID,userId);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + EXPIRATION * 1000);
        String tokenPrefix = Jwts.builder()
                .signWith( SignatureAlgorithm.HS256,JWT_SECRET_KEY)
                .setHeaderParam("type", TOKEN_TYPE)
                .setClaims(map)
                .setIssuer("HCY")
                .setIssuedAt(createdDate)
                .setSubject(username)
                .setExpiration(expirationDate)
                .compact();
        return TOKEN_PREFIX + tokenPrefix;
    }
    /**
     * 令牌是否需要刷新
     * @param token
     * @return
     */
    public static boolean isRefreshToken(String token){
        Date expiredDate = getTokenBody(token).getExpiration();
        return System.currentTimeMillis() > (expiredDate.getTime()- REFRESH_TIME * 1000);
    }

    /**
     * 令牌是否过期
     * @param token
     * @return
     */
    public static boolean isTokenExpired(String token) {
        Date expiredDate = getTokenBody(token).getExpiration();
        return expiredDate.before(new Date());
    }

    public static String getUsernameByToken(String token) {
        return getTokenBody(token).getSubject();
    }
    private static Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }


}
