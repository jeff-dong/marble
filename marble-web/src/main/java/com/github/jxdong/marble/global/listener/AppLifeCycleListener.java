package com.github.jxdong.marble.global.listener;

import com.github.jxdong.marble.common.util.PropertyUtils;

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
    }
}
