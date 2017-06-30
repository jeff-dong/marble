package com.github.jxdong.marble.domain.repositories;


import com.github.jxdong.marble.domain.model.AppDetail;
import com.github.jxdong.marble.domain.model.Page;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.model.ServerDetail;

import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/1/13 14:04
 */
public interface AppRepository extends Repository{

    AppDetail queryAppById(int id);

    AppDetail queryAppByCode(String appCode);

    List<AppDetail> queryAppByMultiConditions(String appCode, String appName, String owner, int[] statusList,  String orderColumn, String orderDir, Page page);

    List<AppDetail> queryAppWithSched(String appCode, int appStatus, String schedName);

    List<ServerDetail> queryServerByAppCode(String appCode);

    Result updateApp(AppDetail appDetail);

    Result insertApp(AppDetail appDetail);

    Result deleteApp(String appCode);

    Result addAppServer(ServerDetail serverDetail);

    Result deleteAppServer(String appCode, String serverGroup, String serverName, String serverIp);

    Result stopApp(String appCode);

    Result startApp(String appCode);

}
