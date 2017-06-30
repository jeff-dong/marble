package com.github.jxdong.marble.agent.entity;

public enum JobExecStatusEnum {

    REQUESTING(0, "请求中"),
    SUCCESS(10, "成功"),
    FAILURE(20, "失败");

    JobExecStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}