package com.vesystem.version.module.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vesystem.version.module.dto.DocShareDto;
import com.vesystem.version.module.entity.DocShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
public interface DocShareMapper extends BaseMapper<DocShare> {

    @Select("SELECT a.*,repos.`repo_name`  FROM ( " +
            "        SELECT * FROM `doc_share` WHERE user_id = #{userId} " +
            "        )a  JOIN `repos` ON a.`repo_id` = repos.`repo_id`")
    List<DocShareDto> selectDocShareListByUserId(Page<DocShareDto> page, @Param("userId") Integer userId);

}
