package com.vesystem.version.constants;

/**
 * @auther hcy
 * @create 2020-08-18 14:58
 * @Description 文件管理系统中会存在大量路径信息需要配置，但是这部分路径名称应该有一定规律，但是又不应该被人为修改
 * 因此全部通过该常量类统一管理.
 * 2020-08-24 添加规范，所有路径中以"/"结尾的表示是文件夹，否则就是文件。各个层级约束好自己，路径前面不在添加"/"
 */
public class PathConstant {


    /**
     * 总的用户缓存相对路径，路径规则应该是：gitRootPath + USER_TEMP_FOLDER_REATIVE_PATH + 用户id
     */
    public static final String USER_TEMP_FOLDER_REATIVE_PATH = "userTemp/";

    /**
     * git仓库的名称前缀，路径规则应该是：gitRootPath + GIT_REPOS_REATIVE_PATH + 仓库id
     */
    public static final String GIT_REPOS_REATIVE_PATH = "Repository/";





}
