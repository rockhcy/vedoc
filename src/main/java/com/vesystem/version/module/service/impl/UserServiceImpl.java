package com.vesystem.version.module.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vesystem.version.constants.PathConstant;
import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import com.vesystem.version.module.dto.UserDto;
import com.vesystem.version.module.entity.User;
import com.vesystem.version.module.mapper.UserMapper;
import com.vesystem.version.module.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vesystem.version.util.CurrentUserUtils;
import com.vesystem.version.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.Objects;

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
    @Autowired
    private CurrentUserUtils currentUserUtils;


    public void updateUserSelfPassword(HttpServletRequest request,String newPwd,String oldPwd){
        User dbUser= userMapper.selectById(JwtToken.getUserIdByRequest(request));
        if (! Objects.equals( dbUser.getPassword(),oldPwd ) ){
            throw new ParameterInvalid(ErrorCode.OLD_PASSWORD_ERROR);
        }
        dbUser.setPassword( newPwd );
        userMapper.updateById(dbUser);
    }

    public void updateUserBaseInfo(UserDto userDto){
        User dbUser= userMapper.selectById(userDto.getUserId());
        dbUser.setAlias( userDto.getAlias() );
        dbUser.setTel( userDto.getTel() );
        dbUser.setEmail( userDto.getEmail() );
        userMapper.updateById(dbUser);
    }

    public User getSelfUserInfo(HttpServletRequest request){
        return userMapper.selectById( JwtToken.getUserIdByRequest(request) );
    }

    public void addUser(HttpServletRequest request,UserDto userDto){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username",userDto.getUsername());
        User dbUser = userMapper.selectOne(qw);
        if ( dbUser !=null ){
            throw new ParameterInvalid(ErrorCode.USER_NAME_ALREADY_EXIST);
        }
        userDto.setCreater( currentUserUtils.getCurrentUser(request).getUsername() );
        userDto.setCreateTime( new Date());
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

    public JSONObject userLogin(String username,String password){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username",username);
        User u = userMapper.selectOne(qw);
        if ( u ==null ){
            throw new ParameterInvalid(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        if ( !Objects.equals(u.getPassword(), password ) ){
            throw new ParameterInvalid(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        String token = JwtToken.createToken(u.getUsername(),u.getRoleId(),u.getAlias(),u.getUserId());
        JSONObject json =new JSONObject();
        json.put("token",token);
        return json;
    }
    public JSONObject refreshToken(HttpServletRequest request){
        String token =JwtToken.refreshToken(request);
        JSONObject json =new JSONObject();
        json.put("token",token);
        return json;
    }



}
