package com.vesystem.version.module.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vesystem.version.enums.ReposTypeEnums;
import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import com.vesystem.version.module.dto.DocShareDto;
import com.vesystem.version.module.entity.DocShare;
import com.vesystem.version.module.entity.Repos;
import com.vesystem.version.module.mapper.DocShareMapper;
import com.vesystem.version.module.mapper.ReposMapper;
import com.vesystem.version.module.service.IDocShareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vesystem.version.util.JwtToken;
import com.vesystem.version.util.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
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
public class DocShareServiceImpl extends ServiceImpl<DocShareMapper, DocShare> implements IDocShareService {
    @Autowired
    private DocShareMapper docShareMapper;
    @Autowired
    private ReposMapper reposMapper;


    public Long produceShareId(){
        return OtherUtils.getSnowflakePrimaryKey();
    }

    public void docShare(HttpServletRequest request,DocShareDto shareDto){
        Repos repos = reposMapper.selectById(shareDto.getRepoId());
        if ( repos == null ){
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        shareDto.setExpireTime( new Date( shareDto.getExpireTimeLong() *1000L ));
        shareDto.setUserId(JwtToken.getUserIdByRequest(request));
        docShareMapper.insert( shareDto );
    }

    public void updateDocShareInfo(DocShareDto shareDto){
        DocShare db = docShareMapper.selectById( shareDto.getShareId() );
        if ( db == null ){
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        // 只允许更新 权限、密码和有效期
        db.setExpireTime(new Date( shareDto.getExpireTimeLong() * 1000L ));
        db.setShareAuth( shareDto.getShareAuth() );
        db.setSharePwd( shareDto.getSharePwd() );
        docShareMapper.updateById(db);
    }

    public IPage<DocShareDto> selectSelfAllDocShareList(HttpServletRequest request, Page<DocShareDto> page){
        Integer userId = JwtToken.getUserIdByRequest(request);
        List<DocShareDto>list = docShareMapper.selectDocShareListByUserId(page,userId);
        page.setRecords( list );
        return page;
    }

    public void deleteDocShareById(Long shareId){
        DocShare docShare = docShareMapper.selectById(shareId);
        if ( docShare == null ){
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        docShareMapper.deleteById(shareId);
    }




}
