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
 * @since 2020-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysApi implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * api表
     */
    @TableId(value = "api_id", type = IdType.AUTO)
    private Integer apiId;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 描述
     */
    private String des;


}
