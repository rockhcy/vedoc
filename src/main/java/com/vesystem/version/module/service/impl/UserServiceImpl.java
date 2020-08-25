package com.vesystem.version.module.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vesystem.version.constants.PathConstant;
import com.vesystem.version.exceptionConfig.ErrorCode;
import com.vesystem.version.exceptionConfig.ParameterInvalid;
import com.vesystem.version.module.dto.UserDto;
import com.vesystem.version.module.entity.User;
import com.vesystem.version.module.dao.UserMapper;
import com.vesystem.version.module.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2020-08-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Value("${gitRootPath}")
    private String gitRootPath;

    public void addUser(UserDto userDto){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username",userDto.getUsername());
        User dbUser = userMapper.selectOne(qw);
        if ( dbUser !=null ){
            throw new ParameterInvalid(ErrorCode.USER_NAME_ALREADY_EXIST);
        }
        userMapper.insert(userDto);
        File file = new File(gitRootPath+ PathConstant.USER_TEMP_FOLDER_REATIVE_PATH + userDto.getUserId() + "/");
        file.mkdirs();
    }

    public void deleteUserById(Integer userId){
        User dbUser =userMapper.selectById(userId);
        if ( dbUser == null ){
            throw new ParameterInvalid(ErrorCode.USER_NOT_EXIST);
        }
        userMapper.deleteById(userId);
        File file = new File(gitRootPath + PathConstant.USER_TEMP_FOLDER_REATIVE_PATH + dbUser.getUserId() + "/");
        file.delete();
    }

    public UserDto getUserInfoById(Integer userId){
        User dbUser =userMapper.selectById(userId);
        if ( dbUser == null ){
            throw new ParameterInvalid(ErrorCode.USER_NOT_EXIST);
        }
        return (UserDto) dbUser;
    }

}
