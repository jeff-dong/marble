package com.github.jxdong.marble.domain.model.enums;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/17 19:23
 */
public enum JobExecStatusEnum {
    SUCCESS(0, "成功"),
    REQUESTING(10, "请求中"),
    FAILURE(20, "失败");

    JobExecStatusEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean containItem(int statusCode){
        if(statusCode<0){
            return false;
        }
        for(JobExecStatusEnum statusEnum : JobExecStatusEnum.values()){
            if(statusEnum.getCode() == statusCode){
                return true;
            }
        }
        return false;
    }

    public static JobExecStatusEnum getItemByCode(int code){
        if(code > 0){
            for(JobExecStatusEnum statusEnum : JobExecStatusEnum.values()){
                if(statusEnum.getCode() == code){
                    return statusEnum;
                }
            }
        }
        return null;
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
