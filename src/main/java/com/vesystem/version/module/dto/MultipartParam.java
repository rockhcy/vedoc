package com.vesystem.version.module.dto;

import lombok.Data;

/**
 * @auther hcy
 * @create 2020-09-02 16:50
 * @Description 分片上传实体类
 */
@Data
public class MultipartParam {

    private String mime_type;//文件类型
    private String name;//文件名称
    private String phase;//状态描述
    private Long size;//文件大小
    private String session_id;//缓存文件的id
    private Long start_offset;//偏移量
    private String path;//文件保存路径
    private String md5;

}
