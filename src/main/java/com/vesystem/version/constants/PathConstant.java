package com.vesystem.version.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Properties;

/**
 * @auther hcy
 * @create 2020-08-18 14:58
 * @Description 文件管理系统中会存在大量路径信息需要配置，但是这部分路径名称应该有一定规律，但是又不应该被人为修改
 * 因此全部通过该常量类统一管理.
 * 2020-08-24 添加规范，所有路径中以"/"结尾的表示是文件夹，否则就是文件。各个层级约束好自己，路径前面不在添加"/"
 */
public class PathConstant {

    private static Logger logger = LoggerFactory.getLogger(PathConstant.class);
    public static String OS_NAME = "";

    public static final String WINDOWS_OS_NAME = "windows";

    static {
        Config();
    }

    /**
     * 总的用户缓存相对路径，路径规则应该是：gitRootPath + USER_TEMP_FOLDER_REATIVE_PATH + 用户id
     */
    public static final String USER_TEMP_FOLDER_REATIVE_PATH = "userTemp/";

    /**
     * git仓库的名称前缀，路径规则应该是：gitRootPath + GIT_REPOS_REATIVE_PATH + 仓库id
     */
    public static final String GIT_REPOS_REATIVE_PATH = "Repository/";
    /**
     * 文件分片阈值
     * 默认为10M
     */
    public static final Long MULTIPART_FILE_THRESHOLD = 1024*1024*10L;


    /**
     * 当系统是windows时对路径经行转换
     * @param path
     * @return
     */
    public static String tranformPathWhenOSIsWindows(String path){
        //不能确定是否所有windows系统都是windows开头的，但是linux的发行版中目前是包含windows字样的
        if ( OS_NAME.toLowerCase().indexOf( WINDOWS_OS_NAME ) ==-1 ){
            return path;
        }else {
            return path.replace("\\","/");
        }
    }



    public static void Config() {
        try {
            Properties props = System.getProperties();
            OS_NAME = props.getProperty("os.name");
        } catch (Exception e) {
            logger.error("获取操作系统类型时发生错误",e);
        }
    }




}
