package com.vesystem.version.module.entity;

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
public class MappingGroupUser implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer groupId;

    private Integer userId;

    /**
     * 1-用户级别，2-管理员级别
     */
    private Integer level;


}
