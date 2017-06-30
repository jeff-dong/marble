package com.github.jxdong.marble.domain.dto;

import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/12/1 16:47
 */
public class CronExpressDTO extends BaseDTO{

    private boolean isValid;
    private String cronExpress;
    //下次执行时间列表
    private List<String> nextFireTimeList;


    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }

    public List<String> getNextFireTimeList() {
        return nextFireTimeList;
    }

    public void setNextFireTimeList(List<String> nextFireTimeList) {
        this.nextFireTimeList = nextFireTimeList;
    }
}
