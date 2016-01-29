package com.github.jxdong.marble.infrastructure.repositories.mapper.mysql;

import com.github.jxdong.marble.domain.model.JobExecutionLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

public interface JobExecutionLogMapper {

    JobExecutionLog selectById(long id) throws DataAccessException;

    List<JobExecutionLog> selectByMultiConditions(Map<String, Object> paramMap) throws DataAccessException;

    int insert(JobExecutionLog jobExecutionLog) throws DataAccessException;

    int update(JobExecutionLog jobExecutionLog) throws DataAccessException;

    int deleteById(long id) throws DataAccessException;

    int deleteJobLog(@Param("appCode") String appCode,@Param("schedName")  String schedName,@Param("jobName")  String jobName) throws DataAccessException;

    //临时数据清理
    int deleteQuartzJobDetail() throws DataAccessException;
    int deleteQuartzCronTriggers() throws DataAccessException;
    int deleteQuartzFiredTriggers() throws DataAccessException;
    int deleteQuartzLocks() throws DataAccessException;
    int deleteQuartzPausedTriggerGroups() throws DataAccessException;
    int deleteQuartzSchedState() throws DataAccessException;
    int deleteQuartzTriggers() throws DataAccessException;
}
