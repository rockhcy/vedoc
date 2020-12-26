package com.vesystem.version.module.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
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
public class DocShare implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 雪花主键
     */
    @TableId
    private Long shareId;

    private String shareName;

    private Integer repoId;

    /**
     * 基于仓库的相对路径
     */
    private String relativePath;

    /**
     * 分享权限
     */
    private Integer shareAuth;

    /**
     * 分享密码
     */
    private String sharePwd;

    /**
     * 分享用户id
     */
    private Integer userId;

    /**
     * 分享有效时间
     */
    private Date expireTime;


}
