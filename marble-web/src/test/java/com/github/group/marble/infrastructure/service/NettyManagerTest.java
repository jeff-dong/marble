package com.github.group.marble.infrastructure.service;

import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.infrastructure.service.netty.WebNettyClientManager;
import com.github.jxdong.marble.agent.entity.ClassInfo;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/15 13:00
 */
public class NettyManagerTest extends TestCase {

    @Test
    public void testCheckConnectivity() throws Exception {
        Result ressult = WebNettyClientManager.getInstance().checkConnectivity("127.0.0.1", 9091);
        System.out.println(ressult);
    }

    @Test
    public void testServiceInvoke() throws Exception {

        Set<ClassInfo> classInfoSet = new HashSet<>();
        classInfoSet.add(new ClassInfo("testJob","execute","参数1"));
        WebNettyClientManager.getInstance().serviceInvoke(StringUtils.genUUID(),"127.0.0.1", 9091, classInfoSet, false, null);

    }
}