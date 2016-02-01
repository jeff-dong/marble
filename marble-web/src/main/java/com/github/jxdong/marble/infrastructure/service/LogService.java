package com.github.jxdong.marble.infrastructure.service;

import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.domain.repositories.LogRepository;
import com.github.jxdong.marble.global.util.SpringContextUtil;

/**
 * 日志服务
 * @author <a href="jxdong@Ctrip.com">jeff</a>
 * @version 2016/2/1 14:37
 */
public class LogService {
    private static LogRepository logRepository;

    static {
        logRepository = (LogRepository)SpringContextUtil.getBean("logRepository", LogRepository.class);
    }

    public void addJobExecutionLog(JobExecutionLog jobExecutionLog){
        logRepository.addJobExecutionLog(jobExecutionLog);
    }

    //单例
    private LogService(){

    }

    public static LogService getInstance(){
        return SingletonHolder.logService;
    }

    private static class SingletonHolder {
        private static final LogService logService = new LogService();
    }

}
