package com.vesystem.version.config;

import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import com.vesystem.version.util.JwtToken;
import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther hcy
 * @create 2020-08-26 14:42
 * @Description
 */
@Component
public class FrontInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(JwtToken.TOKEN_HEADER);
        if ( authHeader == null ){
            throw new ParameterInvalid(ErrorCode.VERIFY_JWT_FAILED);
        }
        if ( JwtToken.isTokenExpired(authHeader.replace(JwtToken.TOKEN_PREFIX,"")) ){
            //令牌过期，退出登陆。刷新令牌由前端自己在请求前置拦截器中处理
            throw new ParameterInvalid(ErrorCode.TOKEN_EXPIRED);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
