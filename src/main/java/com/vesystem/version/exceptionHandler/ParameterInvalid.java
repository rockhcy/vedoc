package com.vesystem.version.exceptionHandler;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther hcy
 * @create 2020-07-11 12:38
 * @Description 参数不合法，请参考API文档
 */
@Data
public class ParameterInvalid extends RuntimeException {
    private ErrorCode errorCode;
    private final transient HashMap<String, Object> data = new HashMap<>();

    public ParameterInvalid(ErrorCode errorCode,Map<String, Object> data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }
    public ParameterInvalid(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }
}
