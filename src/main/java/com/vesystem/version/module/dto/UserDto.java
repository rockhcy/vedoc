package com.vesystem.version.module.dto;

import com.vesystem.version.module.entity.SysRole;
import com.vesystem.version.module.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther hcy
 * @create 2020-08-24 14:00
 * @Description
 */
@Data
public class UserDto extends User {

    private String roleName;

    private SysRole sysRole;


}
