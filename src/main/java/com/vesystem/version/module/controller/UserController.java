package com.vesystem.version.module.controller;

import cn.hutool.json.JSONObject;
import com.vesystem.version.module.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @CrossOrigin
    @PostMapping("userLogin")
    public JSONObject userLogin(String username, String password){
        return userService.userLogin(username,password);
    }



}
