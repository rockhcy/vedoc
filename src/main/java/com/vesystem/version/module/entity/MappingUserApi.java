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
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MappingUserApi implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer userId;

    private Integer apiId;


}
