package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/29 14:45
 */
public class JobLogRequest extends DatatablesRequest{

    private String appCode;
    private String schedName;
    private String jobName;
    private String serverInfo;
    private String beginDate;
    private String endDate;
    private int resultCode;

    //获得排序列信息e.g: order by orderId asc.目前只支持单列排序
    public String getOrderColumn(){
        String column = "id";
        List<Order> orderList = this.getOrder();
        if(ArrayUtils.listIsNotBlank(orderList)){
            String columnIndex = orderList.get(0).getColumn();
            if(StringUtils.isNotBlank(columnIndex)){
                switch (columnIndex){
                    case "1":column = "sched_name"; break;
                    case "2":column = "job_name"; break;
                    case "4":column = "server_info"; break;
                    case "5":column = "dataChange_lastTime"; break;

                }
            }
        }
        return column;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getSchedName() {
        return schedName;
    }

    public void setSchedName(String schedName) {
        this.schedName = schedName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
