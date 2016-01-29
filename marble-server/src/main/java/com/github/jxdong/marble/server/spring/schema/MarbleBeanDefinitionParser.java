package com.github.jxdong.marble.server.spring.schema;

import com.github.jxdong.marble.server.spring.JobBeanConfig;
import com.github.jxdong.marble.server.spring.MarbleSchedulerBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MarbleBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;
    private final boolean required;

    public MarbleBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }

    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);

        //注册ID
        String id = element.getAttribute("id");
        if (id == null || id.length() == 0) {
            throw new IllegalStateException("Spring bean id cannot be empty");
        }
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalStateException("Duplicate spring bean id " + id);
        }
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        beanDefinition.getPropertyValues().addPropertyValue("id", id);

        if (MarbleSchedulerBean.class.equals(beanClass)) {
            //注册Host
            String host = element.getAttribute("host");
            if (host != null && host.length() > 0) {
                beanDefinition.getPropertyValues().addPropertyValue("host", host);
            }

            //注册appCode
            String appCodeStr = element.getAttribute("appCode");
            if (appCodeStr != null && appCodeStr.trim().length() > 0) {
                beanDefinition.getPropertyValues().addPropertyValue("appCode", appCodeStr);
            }


            //注册Port
            String portStr = element.getAttribute("port");
            if (portStr != null && portStr.length() > 0) {
                beanDefinition.getPropertyValues().addPropertyValue("port", portStr);
            }
            //注册Job
            parseJobs(id, element.getChildNodes(), beanDefinition, parserContext);
        }

        return beanDefinition;
    }

    private static void parseJobs(String id, NodeList nodeList, RootBeanDefinition beanDefinition, ParserContext parserContext) {
        if (nodeList != null && nodeList.getLength() > 0) {
            ManagedList jobs = new ManagedList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("job".equals(node.getNodeName()) || "job".equals(node.getLocalName())) {
                        RootBeanDefinition jobBeanDefinition = new RootBeanDefinition();
                        jobBeanDefinition.setBeanClass(JobBeanConfig.class);
                        jobBeanDefinition.setLazyInit(false);

                        //设置Job的name属性
                        String jobName = element.getAttribute("name");
                        if(jobName == null || jobName.length()==0){
                            throw new IllegalStateException("Job name cannot be empty");
                        }
                       //TODO 判断name是否已经存在
                        jobBeanDefinition.getPropertyValues().addPropertyValue("name", jobName);

                        //设置Job的description属性
                        String desc = element.getAttribute("description");
                        if(desc != null && desc.length()>0){
                            jobBeanDefinition.getPropertyValues().addPropertyValue("description", desc);
                        }
                        //设置Job的ref属性
                        String ref = element.getAttribute("ref");
                        if (ref == null || ref.length() == 0) {
                            throw new IllegalStateException("<marble:job> ref == null");
                        }
                        if (parserContext.getRegistry().containsBeanDefinition(ref)) {
                            BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(ref);
//                            if (!refBean.isSingleton()) {
//                                throw new IllegalStateException("The exported marble:job ref (" + ref + ") must be singleton! Please set the " + ref + " bean scope to singleton, eg: <bean id=\"" + ref + "\" scope=\"singleton\" ...>");
//                            }
                            jobBeanDefinition.getPropertyValues().addPropertyValue("ref", refBean);
                        }
                        jobs.add(jobBeanDefinition);
                    }
                }
            }
            if(jobs.size() >0){
                beanDefinition.getPropertyValues().addPropertyValue("jobs", jobs);
            }
        }
    }

}