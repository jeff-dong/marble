package com.github.jxdong.marble.domain.model.enums;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/17 19:23
 */
public enum AppStatusEnum {
    UNKNOWN(0, "未知"),
    USABLE(10, "可用"),
    DISABLE(20, "停用");

    AppStatusEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean containItem(int statusCode){
        for(AppStatusEnum statusEnum : AppStatusEnum.values()){
            if(statusEnum.equals(UNKNOWN)){
                continue;
            }
            if(statusEnum.getCode() == statusCode){
                return true;
            }
        }
        return false;
    }

    public static AppStatusEnum getItemByCode(int code){
        if(code > 0){
            for(AppStatusEnum statusEnum : AppStatusEnum.values()){
                if(statusEnum.getCode() == code){
                    return statusEnum;
                }
            }
        }
        return AppStatusEnum.UNKNOWN;
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
