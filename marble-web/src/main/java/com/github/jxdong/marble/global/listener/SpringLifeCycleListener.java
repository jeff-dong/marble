package com.github.jxdong.marble.global.listener;

import com.github.jxdong.common.util.PropertyUtils;
import com.github.jxdong.marble.global.util.SpringContextUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * @author tangb
 * 该类和TomcatOnOffMonitor为两种不同应用开启和关闭时执行任务的方法。本应用使用Spring 方法。 
 */
@Component
public class SpringLifeCycleListener implements ApplicationListener {
	private static final Logger logger = LoggerFactory.getLogger(SpringLifeCycleListener.class);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if(event instanceof ContextStartedEvent){
			logger.info("The spring starts... , Version: {}", PropertyUtils.getString("Version"));

		}else if(event instanceof ContextRefreshedEvent){
			if(((ContextRefreshedEvent)event).getApplicationContext().getParent() == null) {
				logger.info("The spring starts successfully. Version: {}", PropertyUtils.getString("Version"));
			}

		}else if(event instanceof ContextClosedEvent){
			logger.info("The spring is closing... , Version: {}", PropertyUtils.getString("Version"));
			//关闭Scheduler
			Object object = SpringContextUtil.getBean("marbleScheduler");
			if(object != null){
				try {
					Scheduler scheduler = (Scheduler) object;
					scheduler.shutdown(true);
				} catch (SchedulerException e) {
					logger.info("关闭Quartz Scheduler异常： ", e);
				}
			}

			logger.info("开始关闭Drivers");
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			if(drivers != null){
				while (drivers.hasMoreElements()) {
					Driver driver = drivers.nextElement();
					try {
						DriverManager.deregisterDriver(driver);
						logger.info("De-registering jdbc driver: {}", driver);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			logger.info("Drivers关闭结束");

		}
	}

	
}
