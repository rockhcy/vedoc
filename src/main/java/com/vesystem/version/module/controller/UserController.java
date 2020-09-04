package com.vesystem.version.module.controller;

import cn.hutool.json.JSONObject;
import com.vesystem.version.module.dto.UserDto;
import com.vesystem.version.module.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hcy
 * @create 2020-08-26 10:32
 * @Description
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("addUser")
    public void addUser(HttpServletRequest request,@RequestBody UserDto userDto){
        userService.addUser(request,userDto);
    }

    @DeleteMapping("deleteUserById")
    public void deleteUserById(Integer userId){
        userService.deleteUserById(userId);
    }

    @GetMapping("getUserInfoById")
    public UserDto getUserInfoById(Integer userId){
        return userService.getUserInfoById(userId);
    }

    @PostMapping("userLogin")
    public JSONObject userLogin(@RequestBody UserDto userDto){
        return userService.userLogin(userDto.getUsername(),userDto.getPassword());
    }

    @GetMapping("refreshToken")
    public JSONObject refreshToken(HttpServletRequest request){
        return userService.refreshToken(request);
    }


}
