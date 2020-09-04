package com.vesystem.version.util;

import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther hcy
 * @create 2020-08-24 15:09
 * @Description
 */
public class JwtToken {

    private static Logger log = LoggerFactory.getLogger(JwtToken.class);

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    /**
     * JWT签名密钥
     * 密钥长度必须在 4位以上，否则创建令牌时提示：java.lang.IllegalArgumentException: A signing key must be specified if the specified JWT is digitally signed.
     */
    public static final String JWT_SECRET_KEY = "hechongyang";

    /**
     * 角色的key
     **/
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_ROLE_ID = "roleId";
    public static final String KEY_USER_ID = "userId";

    /**
     * REFRESH_TIME 令牌刷新时间
     * 30分钟  1800L
     */
    public static final long REFRESH_TIME = 18000L;
    /**
     * 令牌过期时间
     * 60分钟
     */
    public static final long EXPIRATION = REFRESH_TIME << 1;

    /**
     * 构建令牌
     * @param username
     * @param roleId
     * @param alias
     * @param userId
     * @return
     */
    public static String createToken(String username, Integer roleId,String alias,Integer userId) {
        Map<String,Object> map = new HashMap<>(4);
        map.put(KEY_USER_NAME,alias);
        map.put(KEY_ROLE_ID, roleId);
        map.put(KEY_USER_ID,userId);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + EXPIRATION * 1000);
        String tokenPrefix = Jwts.builder()
                // SignatureAlgorithm.HS512
                .signWith( SignatureAlgorithm.HS256, JWT_SECRET_KEY )
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
     * 刷新令牌
     * @param request
     * @return
     */
    public static String refreshToken(HttpServletRequest request){
        try {
            String token = request.getHeader(TOKEN_HEADER).replace(TOKEN_PREFIX,"");
            Claims claims = getTokenBody(token);
            return createToken(claims.getSubject(),Integer.valueOf(claims.get( KEY_ROLE_ID ).toString()),
                    claims.get( KEY_USER_NAME ).toString(),Integer.valueOf(claims.get( KEY_USER_ID ).toString()));
        }catch (Exception e){
            log.error("refreshToken err",e);
            throw new ParameterInvalid(ErrorCode.VERIFY_JWT_FAILED);
        }
    }
    /**
     * 令牌是否过期
     * @param token
     * @return true=过期，false=未过期
     */
    public static boolean isTokenExpired(String token) {
        Date expiredDate = getTokenBody(token).getExpiration();
        return expiredDate.before(new Date());
    }

    /**
     *  从HttpServletRequest中获取用户id
     * @param request
     * @return
     */
    public static Integer getUserIdByRequest(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER).replace(TOKEN_PREFIX,"");
        Claims claims = getTokenBody(token);
        return Integer.valueOf( claims.get(KEY_USER_ID).toString() );
    }
    /**
     *  从HttpServletRequest中获取用户id
     * @param request
     * @return
     */
    public static String getUsernameByRequest(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER).replace(TOKEN_PREFIX,"");
        return getTokenBody(token).getSubject();
    }

    private static Claims getTokenBody(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey( JWT_SECRET_KEY )
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            log.error("getTokenBody err",e);
            throw new ParameterInvalid(ErrorCode.VERIFY_JWT_FAILED);
        }
    }


}
