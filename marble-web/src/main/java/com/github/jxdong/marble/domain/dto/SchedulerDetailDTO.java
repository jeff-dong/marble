package com.github.jxdong.marble.domain.dto;

import com.github.jxdong.marble.domain.model.ServerDetail;
import com.github.jxdong.marble.domain.model.enums.SchedStatusEnum;

import java.util.List;

/**
 * 应用信息
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/11 12:58
 */
public class SchedulerDetailDTO extends BaseDTO{

    private AppDetailDTO appDetail;
    private String name;
    private String description;
    private int status;
    private List<JobDetailDTO> jobs;
    private List<ServerDetail> serverDetails;

    public String getName() {
        return name;
    }

    public AppDetailDTO getAppDetail() {
        return appDetail;
    }

    public void setAppDetail(AppDetailDTO appDetail) {
        this.appDetail = appDetail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JobDetailDTO> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDetailDTO> jobs) {
        this.jobs = jobs;
    }

    public List<ServerDetail> getServerDetails() {
        return serverDetails;
    }

    public void setServerDetails(List<ServerDetail> serverDetails) {
        this.serverDetails = serverDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }
    public String getStatusDesc(){
        return SchedStatusEnum.getItemByCode(status).getDesc();
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
