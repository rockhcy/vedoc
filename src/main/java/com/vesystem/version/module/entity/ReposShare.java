package com.vesystem.version.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2020-08-25
 * 用来记录仓库分享记录，只要密码正确就能将对应的仓库添加到自己的mapping_repo_user中
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReposShare implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 仓库分享表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 仓库id
     */
    private Integer repoId;

    /**
     * 仓库密码
     */
    private String repoPwd;


}
