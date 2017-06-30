package com.github.jxdong.marble.domain.model;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/17 13:40
 */
public class Configure extends Entity{

    private int id;
    private String group;
    private String key;
    private String value;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

}
