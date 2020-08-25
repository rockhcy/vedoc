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
public class UserGroup implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 用户组表
     */
    @TableId(value = "group_id", type = IdType.AUTO)
    private Integer groupId;

    /**
     * 组名
     */
    private String groupName;

    /**
     * 组描述
     */
    private String groupDes;


}
