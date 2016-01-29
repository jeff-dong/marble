package com.github.jxdong.marble.domain.model.enums;

/**
 * Configure表的Group分类enum
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/7/2 14:49
 */
public enum ConfigureEnum {

    ROLE_ADMIN(Group.USER_ROLE, "ADMIN", "管理员");

    ConfigureEnum(Group group, String code, String desc){
        this.group = group;
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
    private Group group;

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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public enum Group{
        USER_ROLE("USER_ROLE", "用户角色");

        Group(String code, String desc){
            this.code = code;
            this.desc = desc;
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



}
