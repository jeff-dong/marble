package com.github.jxdong.marble.infrastructure.repositories;

import com.github.jxdong.marble.domain.model.AppDetail;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.model.ServerDetail;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.AppStatusEnum;
import com.github.jxdong.marble.domain.model.enums.ServerStatusEnum;
import com.github.jxdong.marble.domain.repositories.AppRepository;
import com.github.jxdong.marble.global.util.SqlErrorUtil;
import com.github.jxdong.marble.infrastructure.repositories.mapper.mysql.AppMapper;
import com.github.jxdong.marble.infrastructure.service.QuartzManager;
import com.github.jxdong.marble.domain.model.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/14 14:18
 */
@Repository
public class AppRepositoryImpl implements AppRepository {
    private static Logger logger = LoggerFactory.getLogger(AppRepositoryImpl.class);

    @Autowired
    private AppMapper appMapper;
    @Autowired
    private QuartzManager quartzManager;

    @Override
    public AppDetail queryAppById(int id) {
        if (id <= 0) {
            logger.error("Query application by ID({}) failed. The id cannot be empty", id);
            return null;
        }
        try {
            return appMapper.selectAppById(id);
        } catch (Exception e) {
            logger.error("Query application by ID({}) exception. ", id, e);
        }
        return null;
    }

    @Override
    public AppDetail queryAppByCode(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            logger.error("Query application by Code({}) failed. The id cannot be empty", appCode);
            return null;
        }
        try {
            return appMapper.selectAppByCode(appCode);
        } catch (Exception e) {
            logger.error("Query application by appCode({}) exception. ", appCode, e);
        }
        return null;
    }

    @Override
    public List<AppDetail> queryAppByMultiConditions(String appCode, String appName, String owner, int[] statusList, String orderColumn, String orderDir, Page page) {
        List<AppDetail> appDetailList = null;
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appCode", appCode);
            paramMap.put("appName", appName);
            paramMap.put("owner", owner);
            paramMap.put("statusList", statusList);
            paramMap.put("orderColumn", orderColumn);
            paramMap.put("orderDir", orderDir);
            if (page != null) {
                PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
            }
            appDetailList = appMapper.selectAppByMultiConditions(paramMap);
            if (page != null) {
                page.setTotalRecord(appDetailList);
            }
        } catch (Exception e) {
            logger.error("Query application by conditions exception. ", e);
        }
        return appDetailList;
    }

    //查找带scheduler 的所有app列表
    @Override
    public List<AppDetail> queryAppWithSched(String appCode, int appStatus, String schedName) {
        List<AppDetail> appDetailList = null;
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appCode", appCode);
            paramMap.put("appStatus", appStatus);
            paramMap.put("schedName", schedName);
            appDetailList = appMapper.selectAppWithSched(paramMap);
        } catch (Exception e) {
            logger.error("Query AppWithSched exception. ", e);
        }
        return appDetailList;
    }

    @Override
    public List<ServerDetail> queryServerByAppCode(String appCode) {
        List<ServerDetail> serverDetailList = null;
        try {
            serverDetailList = appMapper.selectServerByAppCode(appCode);
        } catch (Exception e) {
            logger.error("Query server appCode({}) exception. ", appCode, e);
        }
        return serverDetailList;
    }

    @Transactional
    @Override
    public Result updateApp(AppDetail appDetail) {
        if (appDetail == null || StringUtils.isBlank(appDetail.getCode())) {
            return Result.FAILURE("Illegal arguments");
        }
        try {
            AppDetail app = this.queryAppByCode(appDetail.getCode());
            //是否更新Marble Version
            boolean updateMarbleVersion = StringUtils.isNotBlank(appDetail.getMarbleVersion()) && !appDetail.getMarbleVersion().equals(app.getMarbleVersion());

            if (app == null) {
                return Result.FAILURE("Can not find the app info by code(" + appDetail.getCode() + ")");
            }
            if (appDetail.getStatus() >= 0 && AppStatusEnum.containItem(appDetail.getStatus())) {
                app.setStatus(appDetail.getStatus());
            }
            if (StringUtils.isNotBlank(appDetail.getName())) {
                app.setName(appDetail.getName());
            }
            if (StringUtils.isNotBlank(appDetail.getDescription())) {
                app.setDescription(appDetail.getDescription());
            }
            if (updateMarbleVersion) {
                app.setMarbleVersion(appDetail.getMarbleVersion());
            }
            if (StringUtils.isNotBlank(appDetail.getOwner())) {
                app.setOwner(appDetail.getOwner());
            }
            if (appDetail.getSoaServiceName()!=null) {
                app.setSoaServiceName(appDetail.getSoaServiceName());
            }
            if (appDetail.getSoaServiceNameSpace()!=null) {
                app.setSoaServiceNameSpace(appDetail.getSoaServiceNameSpace());
            }

            int ret = appMapper.updateAppById(app);
            logger.info("Update app info end. affected records:{}", ret);

            //有MarbleVersion更新
            if(updateMarbleVersion){
                Result result = quartzManager.modifyJobMarbleVersion(appDetail.getCode(), appDetail.getMarbleVersion());
                if(!result.isSuccess()){
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.FAILURE("Quartz更新失败");
                }
            }
            return Result.SUCCESS();
        } catch (DataAccessException e) {
            logger.error("Update app exception, detail info: ", e);
            return Result.FAILURE(SqlErrorUtil.getDataAccessExceptionMsg(e));
        } catch (Exception e) {
            logger.error("Update app exception, detail info: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("Inner Exception");
        }
    }

    @Override
    public Result insertApp(AppDetail appDetail) {
        if (appDetail == null || StringUtils.isBlank(appDetail.getCode()) || StringUtils.isBlank(appDetail.getName()) || StringUtils.isBlank(appDetail.getOwner())) {
            return Result.FAILURE("Illegal arguments");
        }
        String errorMsg = "inner exception";
        try {
            //查找DB中是否存在相同的appID
            AppDetail app = appMapper.selectAppByCode(appDetail.getCode());
            if (app != null) {
                return Result.FAILURE("The app with code (" + appDetail.getCode() + ") has exist.");
            }
            //DB中插入新记录
            appDetail.setStatus(AppStatusEnum.USABLE.getCode());
            appMapper.insertApp(appDetail);
            return Result.SUCCESS();
        }catch (DataAccessException e){
            errorMsg = SqlErrorUtil.getDataAccessExceptionMsg(e);
            logger.error("insert app exception, detail info: ", e);
        } catch (Exception e) {
            logger.error("Update app exception, detail info: ", e);
        }
        return Result.FAILURE("insert app failed. " + errorMsg);
    }

    /**
     * 删除应用
     * 1、删除应用表记录。marble_app
     * 2、删除应用下所有计划任务信息。marble_app_sched
     * 3、删除应用下服务器信息。marble_app_server
     * 4、删除计划任务下服务器信息。marble_server_sched
     * 5、停止应用下所有的Job，并删除。
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteApp(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return Result.FAILURE("Illegal arguments");
        }
        try {
            //查找DB中是否存在
            AppDetail app = appMapper.selectAppByCode(appCode);
            if (app == null) {
                return Result.FAILURE("应用 (" + appCode + ") 不存在");
            }
            //1、删除应用表记录。marble_app
            int ret = appMapper.deleteApp(appCode);
            logger.info("delete ({}) app records from DB", ret);
            //2、删除应用下所有计划任务信息。marble_app_sched
            ret = appMapper.deleteAppSchedByAppCode(appCode);
            logger.info("delete ({}) App Scheduler records from DB", ret);
            //3、删除应用下服务器信息。marble_app_server
            ret = appMapper.deleteAppServerByAppCode(appCode);
            logger.info("delete ({}) App Server records from DB", ret);
            //4、删除计划任务下服务器信息。marble_server_sched
            ret = appMapper.deleteAppShedServerByAppCode(appCode);
            logger.info("delete ({}) App Sched Server records from DB", ret);

            //5、停止应用下所有Job
            Result result = quartzManager.removeJob(appCode, null, null);
            if(!result.isSuccess()){
                logger.error("Delete app failed, rollback. detail info: ", result);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return result;
            }
            return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("Delete app exception, rollback. detail info: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("删除应用(" + appCode + ") 异常："+ e.getMessage());
        }
    }

    /**
     * 添加新的server记录
     * 1、判重；
     * 2、DB中插入记录；
     * @param serverDetail 应用信息
     * @return Result
     */
    @Override
    public Result addAppServer(ServerDetail serverDetail) {
        if (serverDetail == null || !serverDetail.validateParamForInsert()) {
            return Result.FAILURE("参数无效");
        }
        String errorMsg;
        try {
            //查找DB中是否存在相同的server
            ServerDetail server = appMapper.selectServerByGroupName(serverDetail.getAppCode(), serverDetail.getGroup(), serverDetail.getName());
            if (server != null) {
                return Result.FAILURE("服务器 (" + serverDetail.getName() + ") 已经存在");
            }
            //DB中插入新记录
            serverDetail.setStatus(ServerStatusEnum.USABLE.getCode());
            appMapper.insertAppServer(serverDetail);
            return Result.SUCCESS();
        } catch (DataAccessException e){
            errorMsg = SqlErrorUtil.getDataAccessExceptionMsg(e);
            logger.error("insert app server exception, detail info: ", e);
        }catch (Exception e) {
            errorMsg = e.getMessage();
            logger.error("insert app server exception, detail info: ", e);
        }
        return Result.FAILURE("添加应用服务器异常： "+errorMsg);
    }

    /**
     * 删除服务器
     * 1、删除对应的scheduler关系。marble_server_sched
     * 2、删除app下服务器记录。marble_app_server;
     * 3、缓存更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteAppServer(String appCode, String serverGroup, String serverName, String serverIp) {
        String errorMsg;
        try {
            //1、删除对应的scheduler关系。marble_server_sched
            appMapper.deleteAppShedServerByServerIp(appCode, serverGroup, serverIp);
            //2、删除app下服务器记录。marble_app_server
            appMapper.deleteAppServerByServerName(appCode, serverGroup, serverName);
            //3、缓存更新
            Result result = quartzManager.removeJobConnectInfo(appCode, serverIp);
            if(!result.isSuccess()){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.error("delete app server failed. ", result);
                return result;
            }
           return Result.SUCCESS();
        } catch (Exception e) {
            logger.error("delete app server exception. ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorMsg = e.getMessage();
        }
        return Result.FAILURE("删除应用服务器异常：" + errorMsg);
    }

    /**
     * 暂停应用；
     * 1、DB修改App状态为暂停。marble_app
     * 2、应用下的Job全部暂停；pause
     * @param appCode 应用Code
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result stopApp(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return Result.FAILURE("App Code不能为空");
        }
        try {
            AppDetail appDetail = appMapper.selectAppByCode(appCode);
            if (appDetail != null && appDetail.getStatus() == AppStatusEnum.USABLE.getCode()) {
                //1、DB修改App状态为暂停。marble_app
                appDetail.setStatus(AppStatusEnum.DISABLE.getCode());
                appMapper.updateAppById(appDetail);
                //2、停止Job
                Result result = quartzManager.pauseJob(appCode);
                if (!result.isSuccess()) {
                    throw new Exception(result.getResultMsg());
                }
            }
        } catch (Exception e) {
            logger.error("stop app({}) exception. ", appCode, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("停止应用异常：" + e.getMessage());
        }
        return Result.SUCCESS();
    }

    /**
     * 启动应用；
     * 1、DB修改App状态为启用。marble_app
     * 2、应用对应的job全部恢复resume
     *
     * @param appCode 应用Code
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result startApp(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return Result.FAILURE("illegal arguments");
        }
        try{
            AppDetail appDetail = appMapper.selectAppByCode(appCode);
            if (appDetail != null && appDetail.getStatus() == AppStatusEnum.DISABLE.getCode()) {
                //1、DB修改App状态为启用。marble_app
                appDetail.setStatus(AppStatusEnum.USABLE.getCode());
                appMapper.updateAppById(appDetail);
                //2、应用对应的job全部恢复resume
                Result result = quartzManager.resumeJob(appCode);
                if (!result.isSuccess()) {
                    throw new Exception(result.getResultMsg());
                }
            }
        }catch (Exception e){
            logger.error("start app({}) exception. ", appCode, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.FAILURE("启动应用异常：" + e.getMessage());
        }
        return Result.SUCCESS();
    }

}
