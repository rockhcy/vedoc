package com.vesystem.version.exceptionHandler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.HashMap;

@Getter
@ToString
@NoArgsConstructor
public class ErrorReponse {
    private int code;
    private HttpStatus status;
    private String message;
    private String path;
    private Instant timestamp;
    private HashMap<String, Object> errorDetail = new HashMap<>();

    public ErrorReponse(ErrorCode errorCode,String path,HashMap<String, Object> errorDetail){
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.path =path;
        this.timestamp = Instant.now();
        if (!ObjectUtils.isEmpty(errorDetail)) {
            this.errorDetail.putAll(errorDetail);
        }
    }

}
