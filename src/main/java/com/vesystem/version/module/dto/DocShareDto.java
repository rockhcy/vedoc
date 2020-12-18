package com.vesystem.version.module.dto;

import com.vesystem.version.module.entity.DocShare;
import lombok.Data;
import org.apache.ibatis.type.SimpleTypeRegistry;

/**
 * @auther hcy
 * @create 2020-09-30 15:01
 * @Description
 */
@Data
public class DocShareDto extends DocShare {
    /**
     * 有效期 秒数
     */
    private Long expireTimeLong;
    /**
     * 仓库名称
     */
    private String repoName;

}
