package com.vesystem.version.enums;

import lombok.Getter;

/**
 * @auther hcy
 * @create 2020-08-18 16:04
 * @Description 仓库类型，仓库可以分为3类：普通仓库，版本仓库和协作仓库
 * 普通仓库 ：占用空间小，但是不支持版本控制。适合管理私人数据
 * 版本仓库 ：暂用空间较大，支持版本控制，但是不支持多人协作
 * 协作仓库 ：只有管理员能创建，并邀请用户加入。支持版本控制和多人协作，文件采用二阶段提交，需要自己控制冲突（二期）
 */
@Getter
public enum ReposTypeEnums {
    COMMON_REPO(1,"普通仓库"),
    VERSIONS_REPO(2,"版本仓库"),
    COOPERATION_REPO(3,"协作仓库")
    ;

    Integer type;
    String description;

    ReposTypeEnums(Integer type, String description) {
        this.type = type;
        this.description = description;
    }}
