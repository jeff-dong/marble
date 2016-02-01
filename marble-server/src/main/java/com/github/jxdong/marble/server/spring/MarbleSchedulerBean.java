/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jxdong.marble.server.spring;

import com.github.jxdong.marble.server.MarbleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.Serializable;

public class MarbleSchedulerBean<T> extends SchedulerBeanConfig implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener, BeanNameAware, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(MarbleSchedulerBean.class);

    private static transient ApplicationContext SPRING_CONTEXT;

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;

    public MarbleSchedulerBean() {
        super();
    }

    public static ApplicationContext getSpringContext() {
        return SPRING_CONTEXT;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (ContextRefreshedEvent.class.getName().equals(event.getClass().getName())) {
            //启动Netty Server
            MarbleManager.getInstance().startNettyServer();
        }

    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void afterPropertiesSet() throws Exception {
        MarbleManager.getInstance().registerScheduler(this.getId()+"-"+this.getAppCode(), this);

    }

    public void destroy() throws Exception {
        logger.info("close the Thrift server");
        MarbleManager.getInstance().stopServer();
    }

}