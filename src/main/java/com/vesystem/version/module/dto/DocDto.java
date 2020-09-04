package com.vesystem.version.module.dto;

import lombok.Data;

import java.io.File;
import java.util.Date;

/**
 * @auther hcy
 * @create 2020-09-01 20:25
 * @Description 文档对象
 */
@Data
public class DocDto implements Comparable<DocDto>{

    private String name;
    /**
     * 文件类型，1-文件夹；2-文件
     */
    private Integer type;
    private Long size;
    private Long modifyTime;
    private String path;





    @Override
    public int compareTo(DocDto o) {
        return 0;
    }
}
