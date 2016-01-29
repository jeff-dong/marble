package com.github.jxdong.marble.domain.model.enums;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/11/17 19:23
 */
public enum MisfireInstructionEnum {
    IGNORE_MISFIRES(-1, "以错过的第一个频率时间立刻开始执行"),//withMisfireHandlingInstructionIgnoreMisfires
    SMART_POLICY(0, "以当前时间为触发频率立刻触发一次执行"),
    FIRE_ONCE_NOW(1,"以当前时间为触发频率立刻触发一次执行"),//默认值。withMisfireHandlingInstructionFireAndProceed
    DO_NOTHING(2, "不触发立即执行，等待下次Cron触发频率到达");//withMisfireHandlingInstructionDoNothing

    MisfireInstructionEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean containItem(int statusCode){
        for(MisfireInstructionEnum statusEnum : MisfireInstructionEnum.values()){
            if(statusEnum.getCode() == statusCode){
                return true;
            }
        }
        return false;
    }

    public static MisfireInstructionEnum getItemByCode(int code){
        for(MisfireInstructionEnum statusEnum : MisfireInstructionEnum.values()){
            if(statusEnum.getCode() == code){
                return statusEnum;
            }
        }
        return MisfireInstructionEnum.DO_NOTHING;
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
