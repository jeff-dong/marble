package com.github.jxdong.marble.infrastructure.repositories.mapper.mysql;

import com.github.jxdong.marble.domain.model.SchedulerDetail;
import com.github.jxdong.marble.domain.model.ServerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface SchedMapper {

    List<SchedulerDetail> selectAppSched(@Param("appCode") String appCode,@Param("schedName")  String schedName) throws DataAccessException;

    List<ServerDetail> selectSchedServer(@Param("appCode") String appCode,
                                            @Param("schedName")  String schedName,
                                            @Param("serverIp")  String serverIp,
                                            @Param("serverPort")  int serverPort) throws DataAccessException;


    int insertAppSched(SchedulerDetail schedDetail) throws DataAccessException;

    int insertServerSched(ServerDetail serverDetail) throws DataAccessException;

    int deleteSchedById(@Param("id") int id) throws DataAccessException;

    int deleteSchedServerById(@Param("id") int id) throws DataAccessException;

}
