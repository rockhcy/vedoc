package com.vesystem.version.module.dao;

import com.vesystem.version.module.entity.Repos;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

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
}
