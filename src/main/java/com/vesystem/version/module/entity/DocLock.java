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
public class DocLock implements Serializable {

    private static final long serialVersionUID=1L;

    public static final Integer TYPE_FILE = 1;
    public static final Integer TYPE_FOLDER = 2;

    /**
     * 文件锁表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 1-文件锁，2-目录锁
     */
    private Integer type;

    /**
     * 文件或者目录的物理路径
     */
    private String path;

    /**
     * 锁状态，0-无锁，1-锁文件，2-锁目录
     */
    private Integer state;

    /**
     * 加锁人
     */
    private String locker;

    /**
     * 锁定时间，当操过该时间后自动解锁
     */
    private Long lockTime;


}
