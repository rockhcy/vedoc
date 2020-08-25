package com.vesystem.version.module.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author hcy
 * @since 2020-08-24
 *  2020-8-24 调整逻辑：MappingRepoUser只记录映射仓库，用户自己创建的仓库不需要在该表再记录映射。
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MappingRepoUser implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 仓库id
     */
    private Integer repoId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 权限，是否允许访问文件
     */
    private Boolean access;

    /**
     * 权限，是否允许增加文件
     */
    private Boolean add;

    /**
     * 权限，是否允许删除文件
     */
    private Boolean del;

    /**
     * 权限，是否允许修改文件
     */
    private Boolean mod;

    /**
     * 映射名称，允许用户自己定义映射名称，当仓库被删除时提示主仓库已经被删除,
     * 映射名称默认为主仓库名称
     */
    private String mappingName;


}
