package com.vesystem.version.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vesystem.version.module.dao.SysRoleMapper;
import com.vesystem.version.module.dao.UserMapper;
import com.vesystem.version.module.dto.UserDto;
import com.vesystem.version.module.entity.SysRole;
import com.vesystem.version.module.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hcy
 * @create 2020-08-24 15:46
 * @Description
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrentUserUtils {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 该方法会从数据库中查询数据并返回，保证所有数据都是最新的。
     * 原uam中的做法是解析token来获取数据，优点是速度快，不用查数据库，缺点是会有脏读情况存在
     * @param request
     * @return
     */
    public UserDto getCurrentUser(HttpServletRequest request){
        String token = request.getHeader(JwtToken.TOKEN_HEADER);
        String userName = JwtToken.getUsernameByToken(token);
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username",userName);
        UserDto u= (UserDto) userMapper.selectOne(qw);
        SysRole role= sysRoleMapper.selectById(u.getRoleId());
        u.setSysRole(role);
        return u;
    }
}
