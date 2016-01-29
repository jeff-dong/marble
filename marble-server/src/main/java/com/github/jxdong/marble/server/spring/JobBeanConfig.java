package com.github.jxdong.marble.server.spring;


import com.github.jxdong.marble.server.MarbleJob;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/8/20 16:34
 */
public class JobBeanConfig {

    private MarbleJob ref;
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MarbleJob getRef() {
        return ref;
    }

    public void setRef(MarbleJob ref) {
        this.ref = ref;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
