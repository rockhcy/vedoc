package com.vesystem.version.module.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vesystem.version.module.dto.ReposDto;
import com.vesystem.version.module.entity.Repos;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hcy
 * @since 2020-08-24
 */
@Repository
public interface ReposMapper extends BaseMapper<Repos> {

    @Update(" UPDATE `repos` SET repo_path = #{path} WHERE repo_id = #{repoId} ")
    Integer updateRepoPath(@Param("path") String path,@Param("repoId") Integer repoId);

    @Select(" SELECT * FROM `repos` WHERE creater_id = #{createId} ")
    List<ReposDto> selectReposByCreateId(Integer createId);

    @Select(" SELECT * FROM ( " +
            "SELECT * FROM `mapping_repo_user` WHERE user_id = #{userId}) " +
            "a JOIN `repos` ON a.repo_id =  repos.`repo_id` ")
    List<ReposDto> selectMapppingReposListByUserId(Integer userId);

    @Select(" SELECT * FROM `repos` WHERE repo_id IN ( " +
            "SELECT repo_id FROM `mapping_repo_user` WHERE user_id = #{userId}) OR  creater_id = #{userId} ")
    List<ReposDto> selectRepoListByUserId(Page<ReposDto> page,Integer userId);

}
