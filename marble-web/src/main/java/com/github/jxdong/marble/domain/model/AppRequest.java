package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/29 14:45
 */
public class AppRequest extends DatatablesRequest{

    private String appCode;
    private String appName;
    private String appOwner;
    private int[] statusList;

    //获得排序列信息e.g: order by orderId asc.目前只支持单列排序
    public String getOrderColumn(){
        String column = "id";
        List<Order> orderList = this.getOrder();
        if(ArrayUtils.listIsNotBlank(orderList)){
            String columnIndex = orderList.get(0).getColumn();
            if(StringUtils.isNotBlank(columnIndex)){
                switch (columnIndex){
                    case "1":column = "code"; break;
                    case "2":column = "name"; break;
                    case "3":column = "description"; break;
                    case "4":column = "owner"; break;
                    case "5":column = "status"; break;
                    case "6":column = "createTime"; break;
                    case "7":column = "dataChange_lastTime"; break;
                }
            }
        }
        return column;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppOwner() {
        return appOwner;
    }

    public void setAppOwner(String appOwner) {
        this.appOwner = appOwner;
    }

    public int[] getStatusList() {
        return statusList;
    }

    public void setStatusList(int[] statusList) {
        this.statusList = statusList;
    }
}
