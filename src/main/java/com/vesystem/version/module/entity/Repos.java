package com.vesystem.version.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hcy
 * @since 2020-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Repos implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 仓库表
     */
    @TableId(value = "repo_id", type = IdType.AUTO)
    private Integer repoId;

    private String repoName;

    /**
     * 1-普通仓库，2-版本仓库，3-协作仓库
     */
    private Integer repoType;

    /**
     * 仓库在服务器上的物理路径
     */
    private String repoPath;

    /**
     * 仓库简介
     */
    private String repoDes;

    /**
     * 仓库访问密码
     */
    private String repoPwd;

    /**
     * 创建人id,只允许root或者自己删除自己的仓库，
     */
    private Integer createrId;

    /**
     * 仓库创建人
     */
    private String creater;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 远程仓库的地址，仅type=3时有效
     */
    private String remoteAddr;

    /**
     * 远程仓库的用户名，仅type=3时有效
     */
    private String remoteUname;

    /**
     * 远程仓库的密码，仅type=3时有效
     */
    private String remotePwd;




}
