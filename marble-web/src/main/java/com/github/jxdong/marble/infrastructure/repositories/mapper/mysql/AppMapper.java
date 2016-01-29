package com.github.jxdong.marble.infrastructure.repositories.mapper.mysql;

import com.github.jxdong.marble.domain.model.AppDetail;
import com.github.jxdong.marble.domain.model.ServerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

public interface AppMapper {

    AppDetail selectAppById(@Param("id") int id) throws DataAccessException;

    AppDetail selectAppByCode(@Param("appCode") String appCode) throws DataAccessException;

    List<AppDetail> selectAppByMultiConditions(Map<String, Object> params) throws DataAccessException;
    //带scheduler信息的app列表
    List<AppDetail> selectAppWithSched(Map<String, Object> params) throws DataAccessException;

    List<ServerDetail> selectServerBySched(@Param("appCode") String appCode, @Param("schedName") String schedName) throws DataAccessException;
    List<ServerDetail> selectServerByAppCode(@Param("appCode") String appCode) throws DataAccessException;

    ServerDetail selectServerById(@Param("id") int id) throws DataAccessException;

    ServerDetail selectServerByGroupName(@Param("appCode") String appCode, @Param("serverGroup") String serverGroup, @Param("serverName") String serverName) throws DataAccessException;

    int updateAppById(AppDetail appDetail) throws DataAccessException;

    int insertApp(AppDetail appDetail) throws DataAccessException;

    int insertAppServer(ServerDetail serverDetail) throws DataAccessException;

    int deleteApp(@Param("appCode") String appCode) throws DataAccessException;

    int deleteAppSchedByAppCode(@Param("appCode") String appCode) throws DataAccessException;

    int deleteAppServerByAppCode(@Param("appCode") String appCode) throws DataAccessException;

    int deleteAppServerByServerName(@Param("appCode") String appCode, @Param("serverGroup") String serverGroup,@Param("serverName") String serverName) throws DataAccessException;

    int deleteAppShedServerByAppCode(@Param("appCode") String appCode) throws DataAccessException;

    int deleteAppShedServerByServerIp(@Param("appCode") String appCode, @Param("serverGroup") String serverGroup,@Param("serverIp") String serverIp) throws DataAccessException;


}
