package com.github.jxdong.marble.agent.common.util;

import java.util.Hashtable;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/6/27 18:17
 */
public class ClogWrapperFactory {
    private static Hashtable<String, ClogWrapper> loggerCache = new Hashtable<>();

    public static ClogWrapper getClogWrapper(Class classObj) {
        if(classObj == null){
            classObj = ClogWrapper.class;
        }
        ClogWrapper clogWrapper = loggerCache.get(classObj.getName());
        if(clogWrapper == null){
            clogWrapper = new ClogWrapper(classObj);
            loggerCache.put(classObj.getName(), clogWrapper);
        }
        return clogWrapper;
    }
}
