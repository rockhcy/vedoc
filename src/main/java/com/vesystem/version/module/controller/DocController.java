package com.vesystem.version.module.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vesystem.version.module.dto.DocShareDto;
import com.vesystem.version.module.entity.DocShare;
import com.vesystem.version.module.service.impl.DocShareServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hcy
 * @create 2020-09-30 14:58
 * @Description
 */
@RestController
@RequestMapping("share")
public class DocController {

    @Autowired
    private DocShareServiceImpl docShareService;

    @GetMapping("produceShareId")
    public Long produceShareId(){
        return docShareService.produceShareId();
    }

    @PostMapping("docShare")
    public void docShare(DocShareDto shareDto){
        docShareService.docShare(shareDto);
    }

    @PutMapping("updateDocShareInfo")
    public void updateDocShareInfo(DocShareDto shareDto){
        docShareService.updateDocShareInfo(shareDto);
    }

    @GetMapping("selectSelfAllDocShareList")
    public Page<DocShare> selectSelfAllDocShareList(HttpServletRequest request, Page<DocShare> page, String shareName){
        return docShareService.selectSelfAllDocShareList(request,page,shareName);
    }

    @DeleteMapping("deleteDocShareById")
    public void deleteDocShareById(Long shareId){
        docShareService.deleteDocShareById(shareId);
    }

}
