package com.vesystem.version.module.dto;

import com.vesystem.version.module.entity.SysRole;
import com.vesystem.version.module.entity.User;
import lombok.Data;

/**
 * @auther hcy
 * @create 2020-08-24 15:54
 * @Description
 */
@Data
public class SysRoleDto extends SysRole {
    private User user;
    private SysRole sysRole;

}
