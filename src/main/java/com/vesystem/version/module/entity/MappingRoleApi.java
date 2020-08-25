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
public class MappingRoleApi implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer roleId;

    private Integer apiId;


}
