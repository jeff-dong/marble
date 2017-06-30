package com.github.jxdong.marble.domain.model.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/17 19:23
 */
public enum JobStatusEnum {
    UNKNOWN("UNKNOWN", "未知"),
    NONE("NONE", "不存在"),
    NORMAL("NORMAL", "运行中"),
    PAUSED("PAUSED", "暂停"),
    COMPLETE("COMPLETE", "完成"),
    ERROR("ERROR", "异常"),
    BLOCKED("BLOCKED", "被阻止");

    JobStatusEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean containItem(String statusCode){
        for(JobStatusEnum statusEnum : JobStatusEnum.values()){
            if(statusEnum.equals(UNKNOWN)){
                continue;
            }
            if(statusEnum.getCode().equals(statusCode)){
                return true;
            }
        }
        return false;
    }

    public static JobStatusEnum getItemByCode(String code){
        if(StringUtils.isNotBlank(code)){
            for(JobStatusEnum statusEnum : JobStatusEnum.values()){
                if(statusEnum.getCode().equals(code)){
                    return statusEnum;
                }
            }
        }
        return JobStatusEnum.UNKNOWN;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
