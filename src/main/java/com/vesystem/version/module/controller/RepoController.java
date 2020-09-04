package com.vesystem.version.module.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.vesystem.version.module.dto.MultipartParam;
import com.vesystem.version.module.dto.ReposDto;
import com.vesystem.version.module.entity.MappingRepoUser;
import com.vesystem.version.module.entity.Repos;
import com.vesystem.version.module.service.impl.ReposServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @auther hcy
 * @create 2020-09-01 14:34
 * @Description
 */
@RestController
@RequestMapping("repo")
public class RepoController {
    @Autowired
    private ReposServiceImpl reposService;

    @PostMapping("multipartUpload")
    public JSONObject multipartUpload(HttpServletRequest request,MultipartParam param){
        return reposService.multipartUpload(request,param);
    }

    @PostMapping("smallFileUpload")
    public void smallFileUpload(HttpServletRequest request,String savePath){
        reposService.smallFileUpload(request,savePath);
    }

    @GetMapping("getFileList")
    public JSONArray getFileList(String path){
        return reposService.getFileList(path);
    }

    @GetMapping("getHomeReposList")
    @CrossOrigin
    public JSONObject getHomeReposList(HttpServletRequest request){
        return reposService.getHomeReposList(request);
    }

    @PostMapping("addRepo")
    public void addRepo(HttpServletRequest request,@RequestBody ReposDto reposDto){
        reposService.addRepo(request,reposDto);
    }

    @DeleteMapping("deleteSelfRepo")
    public void deleteSelfRepo(HttpServletRequest request,Integer repoId){
        reposService.deleteSelfRepo(request,repoId);
    }

    @GetMapping("getSelfRepoList")
    public List<Repos> getSelfRepoList(HttpServletRequest request){
        return reposService.getSelfRepoList(request);
    }

    @PostMapping("addMappingRepo")
    public void addMappingRepo(HttpServletRequest request,Integer shareId,String password){
        reposService.addMappingRepo(request,shareId,password);
    }

    @DeleteMapping("deleteMappingRepo")
    public void deleteMappingRepo(HttpServletRequest request,Integer repoId){
        reposService.deleteMappingRepo(request,repoId);
    }

    @GetMapping("getMappingRepoList")
    public List<MappingRepoUser> getMappingRepoList(HttpServletRequest request){
        return reposService.getMappingRepoList(request);
    }
}
