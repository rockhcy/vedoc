package com.vesystem.version.util;

import cn.hutool.core.lang.Snowflake;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hcy
 * @create 2020-09-24 10:33
 * @Description
 */
public class OtherUtils {

    /**
     * 从请求中获取项目访问地址前缀
     * @param request
     * @return
     */
    public static String getUrlPrefix(HttpServletRequest request){
        String networkProtocol = request.getScheme();
        String ip = request.getServerName();
        int port = request.getServerPort();
        String webApp = request.getContextPath();
        return  networkProtocol + "://" + ip + ":" + port + webApp;
    }

    /**
     * 获取雪花主键
     * @return
     */
    public static Long getSnowflakePrimaryKey(){
        Snowflake snowflake = new Snowflake(12,12);
        return snowflake.nextId();
    }

}
