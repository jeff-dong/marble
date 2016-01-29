package com.github.jxdong.marble.global.listener;

import com.github.jxdong.common.util.PropertyUtils;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppLifeCycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("=== onStartup (" + PropertyUtils.getString("Version") + ") ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("=== onShutdown (" + PropertyUtils.getString("Version") + ") ===");
        try {
            for (int i = 0; i < 2; i++) {
                AbandonedConnectionCleanupThread.shutdown();
                Thread.sleep(500);
            }
            System.out.println("关闭结束！");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
