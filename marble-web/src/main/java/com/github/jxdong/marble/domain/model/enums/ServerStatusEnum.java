package com.github.jxdong.marble.domain.model.enums;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/17 19:23
 */
public enum ServerStatusEnum {
    UNKNOWN(0, "未知"),
    USABLE(10, "可用"),
    DISABLE(20, "停用");

    ServerStatusEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean containItem(int statusCode){
        for(ServerStatusEnum statusEnum : ServerStatusEnum.values()){
            if(statusEnum.equals(UNKNOWN)){
                continue;
            }
            if(statusEnum.getCode() == statusCode){
                return true;
            }
        }
        return false;
    }

    public static ServerStatusEnum getItemByCode(int code){
        if(code > 0){
            for(ServerStatusEnum statusEnum : ServerStatusEnum.values()){
                if(statusEnum.getCode() == code){
                    return statusEnum;
                }
            }
        }
        return ServerStatusEnum.UNKNOWN;
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
