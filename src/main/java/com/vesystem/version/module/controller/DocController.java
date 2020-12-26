package com.vesystem.version.module.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public void docShare(HttpServletRequest request,@RequestBody DocShareDto shareDto){
        docShareService.docShare(request,shareDto);
    }

    @GetMapping("selectSelfAllDocShareList")
    public IPage<DocShareDto> selectSelfAllDocShareList(HttpServletRequest request, Page<DocShareDto> page){
        return docShareService.selectSelfAllDocShareList(request,page);
    }

    @PutMapping("updateDocShareInfo")
    public void updateDocShareInfo(@RequestBody DocShareDto shareDto){
        docShareService.updateDocShareInfo(shareDto);
    }

    @DeleteMapping("deleteDocShareById")
    public void deleteDocShareById(Long shareId){
        docShareService.deleteDocShareById(shareId);
    }

}
