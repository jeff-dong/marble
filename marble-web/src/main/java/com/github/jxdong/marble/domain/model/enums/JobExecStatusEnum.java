package com.github.jxdong.marble.domain.model.enums;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/17 19:23
 */
public enum JobExecStatusEnum {
    UNKNOWN(0, "未知"),
    SUCCESS(10, "成功"),
    FAILURE(20, "失败");

    JobExecStatusEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean containItem(int statusCode){
        for(JobExecStatusEnum statusEnum : JobExecStatusEnum.values()){
            if(statusEnum.equals(UNKNOWN)){
                continue;
            }
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
        return JobExecStatusEnum.UNKNOWN;
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
