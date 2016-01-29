package com.github.jxdong.marble.domain.model.enums;

public enum ErrorEnum {

    OTHER_ERROR("100", "其它错误", 10),
    NETWORK_ERROR("101", "网络错误", 10),
    SESSON_OVERDUE("102", "session过期", 10),
    ILLEGAL_ARGUMENT("103", "参数非法", 10),
    NO_PERMISSION("104", "没有权限", 10);

    private String code;
    private String message;
    private int errorLevel;

    ErrorEnum(String code, String message, int errorLevel){
        this.code = code;
        this.message = message;
        this.errorLevel = errorLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public int getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    public void setCode(String code) {
        this.code = code;
    }

}