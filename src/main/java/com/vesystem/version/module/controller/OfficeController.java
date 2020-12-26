package com.vesystem.version.module.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import com.vesystem.version.module.service.impl.ReposServiceImpl;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Scanner;

/**
 * @auther hcy
 * @create 2020-09-23 16:44
 * @Description
 */
@RestController
@RequestMapping("office")
public class OfficeController {
    @Autowired
    private ReposServiceImpl reposService;

    @GetMapping("getPage")
    public JSONObject getPage2(String fileType, String title, String filePath, HttpServletRequest request){
        return reposService.getPage(fileType, title, filePath, request);
    }

    @RequestMapping("editorCallBack")
    public void onlineEditorDoc(HttpServletRequest request, String filePath) {
        reposService.onlineEditorDoc(request, filePath);
    }

}
