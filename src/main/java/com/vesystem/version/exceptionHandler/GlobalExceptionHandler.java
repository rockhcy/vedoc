package com.vesystem.version.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


/**
 * @author shuang.kou
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 参数不符合api文档规范
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = ParameterInvalid.class)
    public ResponseEntity<ErrorReponse> handleParameterInvalid(ParameterInvalid ex, HttpServletRequest request) {

        ErrorReponse errorReponse = new ErrorReponse(ex.getErrorCode(), request.getRequestURI(),ex.getData());
        log.error("occur ParameterInvalid:" + errorReponse.toString());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorReponse);
    }

}
