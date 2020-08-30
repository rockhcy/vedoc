package com.vesystem.version.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vesystem.version.constants.PathConstant;
import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import com.vesystem.version.module.dao.MappingRepoUserMapper;
import com.vesystem.version.module.dao.ReposShareMapper;
import com.vesystem.version.module.dto.ReposDto;
import com.vesystem.version.module.dto.UserDto;
import com.vesystem.version.module.entity.MappingRepoUser;
import com.vesystem.version.module.entity.Repos;
import com.vesystem.version.module.dao.ReposMapper;
import com.vesystem.version.module.entity.ReposShare;
import com.vesystem.version.module.service.IReposService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vesystem.version.util.CurrentUserUtils;
import com.vesystem.version.util.GitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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
public class ReposServiceImpl extends ServiceImpl<ReposMapper, Repos> implements IReposService {
    @Autowired
    private CurrentUserUtils currentUserUtils;
    @Autowired
    private ReposMapper reposMapper;
    @Autowired
    private MappingRepoUserMapper mappingRepoUserMapper;

    @Autowired
    private ReposShareMapper reposShareMapper;

    @Value("${gitRootPath}")
    private String gitRootPath;

    public void addRepo(HttpServletRequest request,ReposDto reposDto){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        reposDto.setCreater(userDto.getUsername());
        reposDto.setCreateTime(new Date());
        reposDto.setCreaterId(userDto.getUserId());
        reposMapper.insert(reposDto);
        reposDto.setRepoPath( gitRootPath + PathConstant.GIT_REPOS_REATIVE_PATH + reposDto.getRepoId() + "/");
        if ( !GitUtil.initGitRepository(reposDto) ){
            throw new ParameterInvalid(ErrorCode.INIT_REPOS_ERROR);
        }
        //注意这里使用先插入在更新的逻辑，放弃查询最后一条记录将id+1的处理方式,避免并发id被其他线程使用
        reposMapper.updateRepoPath(reposDto.getRepoPath(),reposDto.getRepoId());
    }

    public void addMappingRepo(HttpServletRequest request,Integer shareId,String password){
        ReposShare rs= reposShareMapper.selectById(shareId);
        if (rs ==null){
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        if ( !StringUtils.isEmpty( rs.getRepoPwd() ) && !Objects.equals( rs.getRepoPwd(),password ) ){
            throw new ParameterInvalid(ErrorCode.REPO_SHARE_PASSWORD_ERROR);
        }
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        Repos r = reposMapper.selectById( rs.getRepoId() );
        //分享他人仓库，默认权限为：1 1 0 1
        mappingRepoUserMapper.insert(new MappingRepoUser(rs.getRepoId(),userDto.getUserId(),true,true,false,true,r.getRepoName()));
    }

    public void deleteSelfRepo(HttpServletRequest request,Integer repoId){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        Repos dbRepo = reposMapper.selectById(repoId);
        if ( dbRepo == null ){
            throw new ParameterInvalid(ErrorCode.REPO_NOT_EXIST);
        }
        if ( !Objects.equals( userDto.getUserId(),dbRepo.getCreaterId() )  && !Objects.equals( userDto.getUserId(),"1" )){
            //只允许删除自己创建的仓库，root拥有特权
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        QueryWrapper<Repos> qw = new QueryWrapper<>();
        qw.eq("repo_id",dbRepo.getRepoId());
        reposMapper.delete(qw);
    }

    public void deleteMappingRepo(HttpServletRequest request,Integer repoId){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        QueryWrapper<MappingRepoUser> qw = new QueryWrapper<>();
        qw.eq("repo_id",repoId).eq("user_id",userDto.getUserId() );
        mappingRepoUserMapper.delete(qw);
    }

    public List<Repos> getSelfRepoList(HttpServletRequest request){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        QueryWrapper<Repos> qw = new QueryWrapper<>();
        qw.eq("creater_id",userDto.getUserId());
        return reposMapper.selectList( qw );
    }

    public List<MappingRepoUser> getMappingRepoList(HttpServletRequest request){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        QueryWrapper<MappingRepoUser> qw = new QueryWrapper<>();
        qw.eq("user_id",userDto.getUserId());
        return mappingRepoUserMapper.selectList( qw );
    }



}
