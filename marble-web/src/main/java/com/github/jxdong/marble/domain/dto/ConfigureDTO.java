package com.github.jxdong.marble.domain.dto;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/26 9:12
 */
public class ConfigureDTO extends BaseDTO{

    private String group;
    private String key;
    private String value;
    private String description;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
