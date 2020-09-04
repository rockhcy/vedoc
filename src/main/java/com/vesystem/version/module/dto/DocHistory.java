package com.vesystem.version.module.dto;

import lombok.Data;

/**
 * @auther hcy
 * @create 2020-09-03 15:25
 * @Description doc历史对象
 */
@Data
public class DocHistory {
    /**
     * 提交信息
     */
    private String commitMsg;
    /**
     * 提交时间
     */
    private Long time;
    /**
     * 提交objectId
     */
    private String objectId;
    /**
     * 提交人
     */
    private String submitter;

    /**
     * 归属仓库的id
     */
    private Integer repoId;

    /**
     * 仓库路径
     */
    private String repoPath;

    /**
     * 条目(doc)名称
     */
    private String entryName;

}
