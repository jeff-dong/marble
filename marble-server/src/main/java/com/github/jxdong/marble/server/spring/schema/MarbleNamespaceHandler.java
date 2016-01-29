package com.github.jxdong.marble.server.spring.schema;

import com.github.jxdong.marble.server.spring.MarbleSchedulerBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MarbleNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {  
        registerBeanDefinitionParser("scheduler", new MarbleBeanDefinitionParser(MarbleSchedulerBean.class, true));
    }  
}  