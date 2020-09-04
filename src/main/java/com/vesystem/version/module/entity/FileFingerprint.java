package com.vesystem.version.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hcy
 * @since 2020-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileFingerprint implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 只记录10M以上的大文件
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件hash
     */
    private String hash;

    /**
     * 文件是否还存在
     */
    private Boolean isExist;


}
