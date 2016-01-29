package com.github.jxdong.marble.domain.model;

import com.github.jxdong.common.util.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/29 14:45
 */
public class ConfigureRequest extends DatatablesRequest{

    private String[] group;
    private String[] key;

    //获得排序列信息e.g: order by orderId asc.目前只支持单列排序
    public String getOrderColumn(){
        String column = "id";
        List<Order> orderList = this.getOrder();
        if(ArrayUtils.listIsNotBlank(orderList)){
            String columnIndex = orderList.get(0).getColumn();
            if(StringUtils.isNotBlank(columnIndex)){
                switch (columnIndex){
                    case "1":column = "group"; break;
                    case "2":column = "key"; break;
                    case "3":column = "value"; break;
                    case "4":column = "createTime"; break;
                    case "5":column = "dataChange_lastTime"; break;

                }
            }
        }
        return column;
    }

    public String[] getGroup() {
        return group;
    }

    public void setGroup(String[] group) {
        this.group = group;
    }

    public String[] getKey() {
        return key;
    }

    public void setKey(String[] key) {
        this.key = key;
    }
}
