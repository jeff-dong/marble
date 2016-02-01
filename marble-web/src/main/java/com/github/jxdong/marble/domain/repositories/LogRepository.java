package com.github.jxdong.marble.domain.repositories;


import com.github.jxdong.marble.domain.model.JobExecutionLog;
import com.github.jxdong.marble.domain.model.Page;
import com.github.jxdong.marble.domain.model.Result;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/1/13 14:04
 */
public interface LogRepository extends Repository{


    JobExecutionLog queryJobExecutionLogById(long id);

    List<JobExecutionLog> queryJobExecutionLog(String appCode,
                                               String schedName,
                                               String jobName,
                                               String serverInfo,
                                               String beginDate,
                                               String endDate,
                                               int resultCode,
                                               String orderColumn, String orderDir, Page page);

    Result addJobExecutionLog(JobExecutionLog jobExecutionLog);

    Result deleteJobExecutionLog(String appCode, String schedName, String jobName);

    Result deleteJobExecutionLog(long id);

    Result clearQuartzDBData();
}
