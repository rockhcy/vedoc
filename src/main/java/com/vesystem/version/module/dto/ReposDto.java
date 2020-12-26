package com.vesystem.version.module.dto;

import com.vesystem.version.module.entity.MappingRepoUser;
import com.vesystem.version.module.entity.Repos;
import lombok.Data;

/**
 * @auther hcy
 * @create 2020-08-24 15:06
 * @Description
 */
@Data
public class ReposDto extends Repos {

    /**
     *  是否为自己创建的仓库
     */
    private Boolean isSelfCreate;

    /**
     *  映射仓库信息
     */
    private MappingRepoUser mappingRepoUser;



}
