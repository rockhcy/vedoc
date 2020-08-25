package com.vesystem.version.exceptionConfig;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @auther hcy
 * @create 2020-08-24 14:07
 * @Description
 */
@Getter
public enum ErrorCode {
    USER_NAME_ALREADY_EXIST(1001, HttpStatus.INSUFFICIENT_STORAGE, "账号已经存在"),
    ACCOUNT_PASSWORD_ERROR(1002, HttpStatus.INSUFFICIENT_STORAGE, "账号或密码错误"),
    VERIFY_JWT_FAILED(1003, HttpStatus.UNAUTHORIZED, "token不合法"),
    TOKEN_EXPIRED(1004, HttpStatus.UNAUTHORIZED, "token过期"),
    TOKEN_REFRESH(1005, HttpStatus.UNAUTHORIZED, "token需要刷新"),
    SYSTEM_EXPIRES(1006,HttpStatus.SERVICE_UNAVAILABLE,"系统授权已过期，请联系管理员!"),
    JAVA_SERVICE_NOT_SUPPORT_UTF_8(1007,HttpStatus.VARIANT_ALSO_NEGOTIATES,"系统服务器没有找到UTF8字符集支持库"),
    METHOD_ARGUMENT_NOT_VALID(1008, HttpStatus.NOT_IMPLEMENTED, "方法参数验证失败,请参考API文档"),
    USER_NOT_EXIST(1009, HttpStatus.INSUFFICIENT_STORAGE, "账号已经存在"),
    INIT_REPOS_ERROR(1010, HttpStatus.INSUFFICIENT_STORAGE, "初始化本版仓库时发生错误"),
    REPO_NOT_EXIST(1011, HttpStatus.INSUFFICIENT_STORAGE, "仓库不存在"),
    REPO_SHARE_PASSWORD_ERROR(1012, HttpStatus.INSUFFICIENT_STORAGE, "仓库的分享密码错误"),
    ;

    ;


    private final int code;

    private final HttpStatus status;

    private final String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}