<style type="text/css">
    table.panel-tb tr.child-thead {
        font-weight: bolder;
        background-color: #eee;
        color: #636060;
    }
    #jq-datatable td{
        max-width:150px;
    }
</style>
<script>
    var datatableSchedManage;
    init.push(function () {
        datatableSchedManage = $('#jq-datatable').DataTable({
            "processing": false,
            "searching": false,
            "ordering": false,
            "paging": false,
            "serverSide": true,
            "autoWidth": false,
            "ajax": {
                "url": "${basePath}/app/scheduler/query",
                "dataSrc": "result",
                "type": "post",
                "data": function (data) {
                    return "_method_=get&appCode=${APP_CODE}&jsonParam=" + JSON.stringify(data);
                }, "error": function (data) {
                    var errorMsg = (data == null || data.responseText == null) ? "未知错误" : data.responseText;
                    $("#jq-datatable tbody").html('<tr><td colspan="8" class="error-msg">' + errorMsg + '</td></tr>');
                }
            },
            "dom": 't',
            "columns": [
                {
                    "data": function (row) {
                        var appCode = $("#sched-panel-manage").attr("appcode");
                        return '<a appcode="' + appCode + '" onclick="javascript:populateSchedulerJobs(this,\'' + appCode + '\',\'' + row.name + '\', false);return false;" schedname="' + row.name + '" class="row-details" style="cursor: pointer;font-size: 14px;font-weight: bolder;">' +
                                '<span class="btn-label icon fa fa-plus-square"></span>&nbsp;Jobs' +
                                '</a>';
                    }, "width": 70, "orderable": false
                },
                {"data": "name"},
                {"data": "description"},
                {"data": "status"},
                {
                    "data": function (row) {
                        var serverDiv = '';
                        if (row.serverDetails != null && row.serverDetails.length > 0) {
                            for (var index in row.serverDetails) {
                                serverDiv = serverDiv + row.serverDetails[index].ip + ':' + row.serverDetails[index].port + '\n';
                            }
                        }
                        return serverDiv;
                    }, "width": 70
                },
                {"data": "createTime"},
                {"data": "updateTime"}
            ],
            "columnDefs": [{
                "targets": [7],
                "render": function (data, type, full) {
                    var appCode = $("#sched-panel-manage").attr("appcode");
                    return '<button class="btn btn-info edit-btn" onclick="schedOperate(this,\'' + appCode + '\',\'' + full.name + '\',\'EDIT\')">编辑</button>&nbsp;' +
                            '<button class="btn btn-outline btn-labeled btn-danger" onclick="schedOperate(this,\'' + appCode + '\',\'' + full.name + '\',\'DELETE\')">' +
                            '<span class="btn-label icon fa  fa-trash-o"></span>删除' +
                            '</button> &nbsp;' +
                            '<div class="btn-group">' +
                            '<button type="button" class="btn btn-primary">更多操作</button>' +
                            '<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fa fa-caret-down"></i></button>' +
                            '<ul class="dropdown-menu">' +
                            '<li><a href="javascript:popupAddJobDialog(this,\'' + full.name + '\');"><span class="btn-label icon fa fa-plus"/>&nbsp;添加Job</a></li>' +
                            '<li class="divider"></li>' +
                            '<li><a href="javascript:popupCheckServerDialog(this,\'' + full.name + '\');"><span class="btn-label icon fa fa-check-circle"/>&nbsp;检查Server连通性</a></li>' +
                            '</ul>' +
                            '</div>';
                }
            }],
            "initComplete": function (settings, json) {
            },
            "order": [[1, 'asc']]
        });
        $("#search-btn").click(function () {
            var appCode = $("#sched-panel-manage").attr("appcode");
            datatableSchedManage.ajax.url("${basePath}/app/scheduler/query?appCode=" + appCode).load();
        });
    });

    function populateServerForSched(appCode, schedName, jobName, page){
        if(appCode == null || schedName == null){
            return false;
        }
        $.ajax({
            "url": "${basePath}/app/scheduler/server/query",
            "data":{"_method_":"get","appCode":appCode,"schedName":schedName},
            "type":"POST",
            "contentType":"application/x-www-form-urlencoded;charset=UTF-8",
            "error": function () {
            },
            "success": function (response) {
                if (response != null && '0000' == response.resultCode) {
                    var serverList =  response.result;
                    var serverDiv = '';
                    for(var index in serverList){
                        var tempEle = '';
                        var tempName = schedName;
                        if(page == 'stopjob'){
                            tempName = jobName;
                            tempEle = '<button class="btn btn-sm btn-labeled btn-success" ' +
                                    'onclick="jobStopImmediatelyOneServer(\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'' + serverList[index].ip + '\')"' +
                                    '><span class="btn-label icon fa  fa-stop"></span>尝试中断</button>';
                        }
                        serverDiv = serverDiv + '<tr><td>'+tempName+'</td><td>'+serverList[index].ip+'</td><td>'+serverList[index].port+'</td><td>'+tempEle+'</td></tr>';
                    }
                    if(serverDiv != ''){
                        if(page == 'stopjob'){
                            $("#job-server-list-tb tbody").html(serverDiv);
                        }else{
                            $("#server-list-tb tbody").html(serverDiv);
                        }
                    }
                }
            }
        });
    }
    //检查server连通性popup
    function popupCheckServerDialog(obj, schedName) {
        var appCode = $("#sched-panel-manage").attr("appcode");
        populateServerForSched(appCode, schedName,null,"checkconnection");
        //查找scheduler下的server
        $("#serverCheckHealthDialog").modal();
    }

    //添加job popup
    function popupAddJobDialog(obj, schedName) {
        $("#jq-validation-job-form span.add-schedname").html(schedName);
        $("#addNewJobDialog").modal();
    }
    //填充每一行的子行
    function populateSchedulerJobs(obj, appcode, schedname, defaultStatus) {
        if (obj == null || appcode == null || schedname == null) {
            return "";
        }
        var nTr = $(obj).parents('tr')[0];
        var row = datatableSchedManage.row(nTr);

        if (!defaultStatus && row.child.isShown()) {
            row.child.hide();
            $(obj).children("span.btn-label").removeClass("fa-minus-square");
            $(obj).children("span.btn-label").addClass("fa-plus-square");
        } else {
            row.child(getSchedJobsList(appcode, schedname)).show();
            $(obj).children("span.btn-label").removeClass("fa-plus-square");
            $(obj).children("span.btn-label").addClass("fa-minus-square");
        }
    }

    //查找Scheduler下的jobs
    function getSchedJobsList(appcode, schedname) {
        var result = '';
        if (appcode == null || schedname == null) {
            return result;
        }
        result = '<div class="col-md-6" style="width:98%;"><table class="panel-tb table table-bordered detail-table" style="clear: both">' +
                '<tr class="child-thead">' +
                '<td>Job名称</td>' +
                '<td>Job描述</td>' +
                '<td>执行类型</td>' +
                '<td>CRON表达式</td>' +
                '<td>参数</td>' +
                '<td>开始时间</td>' +
                '<td>结束时间</td>' +
                '<td>上次触发时间</td>' +
                '<td>下次触发时间</td>' +
                '<td>Job状态</td>' +
                '<td style="width:240px;">操作</td></tr><tbody>';

        $.ajax({
            "url": "${basePath}/app/scheduler/job/query",
            "data": {"_method_": "get", "appCode": appcode, "schedName": schedname},
            "type": "POST",
            "async": false,
            "error": function () {
            },
            "success": function (response) {
                if (response != null && '0000' == response.resultCode) {
                    var jobList = response.result;
                    for (var index in jobList) {
                        var tempJob = jobList[index];
                        result = result +
                                '<tr>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.name, "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.description, "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.synchronous ? "同步(" + safeString(tempJob.maxWaitTime, "") + ")" : "异步", "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.cronExpress, "N/A") + '</td>' +
                                '<td class="detail-tb-title" style="max-width:200px;">' + safeString(tempJob.param, "N/A", 30) + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.startTime, "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.endTime, "") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.prevFireTime, "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.nextFireTime, "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + safeString(tempJob.statusDesc, "N/A") + '</td>' +
                                '<td class="detail-tb-title">' + getJobOperateBTNByStatus(tempJob.status, appcode, schedname, tempJob.name) + '</td>' +
                                '</tr>';
                    }
                }
            }
        });
        return result + '</tbody></table></div>';
    }

    //Scheduler操作
    function schedOperate(obj, appCode, schedName, operateType) {
        if (obj == null || appCode == null || schedName == null || operateType == null) {
            return false;
        }

        switch (operateType) {
            case 'EDIT':
                $("#editSchedulerDialog").modal();
                return;
            case 'DELETE':
                deleteSched(appCode, schedName);
                return;
            default:
                return;
        }
    }

    //手动执行一次JOB
    function jobExecuteOnce(appCode, schedName, jobName) {
        if (appCode == null || schedName == null || jobName == null) {
            alert("执行失败,请刷新页面重试");
            return false;
        }
        //填充固定字符
        $("#exec-appcode-input").html(appCode);
        $("#exec-schedName-input").html(schedName);
        $("#exec-jobName-input").html(jobName);

        $("#execSchedJobDialog").modal();
    }

    function jobStopImmediately(appCode, schedName, jobName) {
        if (appCode == null || schedName == null || jobName == null) {
            alert("执行失败,请刷新页面重试");
            return false;
        }
        populateServerForSched(appCode, schedName,jobName,"stopjob");
        //查找scheduler下的server
        $("#stopSchedJobDialog").modal();
    }
    function jobStopImmediatelyOneServer(appCode, schedName, jobName, serverIp) {
        if (appCode == null || schedName == null || jobName == null || serverIp == null) {
            alert("执行失败,请刷新页面重试");
            return false;
        }
        $.ajax({
            "url": "${basePath}/app/scheduler/job/stopImmediately",
            "data": {"_method_": "POST", "appCode": appCode, "schedName": schedName, "jobName":jobName,"serverIp":serverIp},
            "type": "POST",
            "async": false,
            "error": function () {
            },
            "success": function (response) {
                if (response != null && '0000' == response.resultCode) {
                    alert('成功');

                }
            }
        });
    }

    //删除Scheduler
    function deleteSched(appCode, schedName) {
        if (appCode == null || schedName == null) {
            return false;
        }
        bootbox.dialog({
            message: '<div style="color:red;font-size: 14px;">计划任务（' + schedName + '）被删除后，相关的Job将会被一同移除且不可恢复！</div>',
            title: '删除提示',
            buttons: {
                success: {
                    label: "取消",
                    className: "btn-cancel",
                    callback: function () {
                    }
                },
                danger: {
                    label: "确定删除",
                    className: "btn-danger",
                    callback: function () {
                        $.ajax({
                            "url": "${basePath}/app/scheduler/delete",
                            "async": false,
                            "data": {
                                "_method_": "delete",
                                "appCode": appCode,
                                "schedName": schedName
                            },
                            "type": "POST",
                            "error": function () {
                                $.growl.error({title: "操作失败：", message: "未知错误，请刷新页面后重试"});
                            },
                            "success": function (response) {
                                if (response != null && '0000' != response.resultCode) {
                                    $.growl.error({title: "删除失败：", message: response.resultMessage});
                                } else {
                                    location.reload();
                                }
                            }
                        });
                    }
                }
            },
            className: "bootbox-sm"
        });
    }

    //Job操作
    function jobOperate(obj, appCode, schedName, jobName, operateType) {
        if (obj == null || appCode == null || schedName == null || jobName == null || operateType == null) {
            return '';
        }
        var operateM = null;
        switch (operateType) {
            case 'STOP':
                operateM = 'stop';
                break;
            case 'START':
                operateM = 'start';
                break;
            case 'EDIT':
                populateJobInfoForEditDialog(appCode, schedName, jobName);
                $("#editJobDialog").modal();
                return;
            case 'DELETE':
                deleteJob(appCode, schedName, jobName);
                return;
            case 'EXECUTE_ONCE':
                jobExecuteOnce(appCode, schedName, jobName);
                return;
            case 'STOP_IMME':
                jobStopImmediately(appCode, schedName, jobName);
                return;
            default:
                return;
        }

        //刷新JOB展示列表
        $.ajax({
            "url": "${basePath}/app/scheduler/job/" + operateM,
            "async": false,
            "data": {
                "_method_": "patch",
                "appCode": appCode,
                "schedName": schedName,
                "jobName": jobName
            },
            "type": "POST",
            "error": function () {
                $.growl.error({title: "操作失败：", message: "未知错误"});
            },
            "success": function (response) {
                if (response != null && '0000' == response.resultCode) {
                    $.growl.notice({title: "提示：", message: "操作 (" + operateM + ") 成功！"});
                    //刷新table
                    populateSchedulerJobs($(obj).parents('tr').prev('tr').find("a.row-details")[0], appCode, schedName, true);
                } else {
                    $.growl.error({title: "错误提示：", message: "操作 (" + operateM + ") 失败！ " + response.resultMessage});
                }
            }
        });
    }
    //根据job状态生成按钮列表
    function getJobOperateBTNByStatus(statusCode, appCode, schedName, jobName) {
        var btnDiv = '';
        if (statusCode != null) {
            switch (statusCode) {
                    //运行中
                    //暂停
                case 'PAUSED' :
                    btnDiv = '<button onclick="jobOperate(this,\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'START\')" class="btn btn-success btn-labeled">' +
                            '<span class="btn-label icon fa fa-play"></span>开始' +
                            '</button>';
                    break;
                default:
                    btnDiv = '<button onclick="jobOperate(this,\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'STOP\')" class="btn btn-warning btn-labeled">' +
                            '<span class="btn-label icon fa fa-pause"></span>暂停调度 ' +
                            '</button>';
                    break;
            }
        }
        btnDiv = btnDiv +
                '&nbsp;<button class="btn btn-info edit-btn btn-labeled " onclick="jobOperate(this,\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'EDIT\')">' +
                '<span class="btn-label icon fa  fa-edit"></span>编辑' +
                '</button>&nbsp;' +
                '<div class="btn-group">' +
                '<button type="button" class="btn btn-primary">...</button>' +
                '<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fa fa-caret-down"></i></button>' +
                '<ul class="dropdown-menu">' +
                '<li><a href="javascript:jobOperate(this,\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'EXECUTE_ONCE\');"><span class="btn-label icon fa fa-play"/>&nbsp;手动调度一次</a>' +
                '</li>' +
                '<li>' +
                '<a href="javascript:jobOperate(this,\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'STOP_IMME\');"><span class="btn-label icon fa fa-stop"/>&nbsp;手动线程中断</a>' +
                '</li>' +
                '<li class="divider"></li>' +
                '<li><a href="javascript:jobOperate(this,\'' + appCode + '\',\'' + schedName + '\',\'' + jobName + '\',\'DELETE\');"><span class="btn-label icon fa fa-trash-o"/>&nbsp;删除</a></li>' +
                '</ul>' +
                '</div>';
        return btnDiv;
    }
    //删除Job
    function deleteJob(appcode, schedName, jobName) {
        bootbox.confirm({
            message: "确定要删除Job【" + jobName + "】？",
            callback: function (result) {
                if (result) {
                    $.ajax({
                        "url": "${basePath}/app/scheduler/job/delete",
                        "data": {"_method_": "delete", "appCode": appcode, "schedName": schedName, "jobName": jobName},
                        "type": "post",
                        "error": function () {
                        },
                        "success": function (response) {
                            if (response != null && '0000' != response.resultCode) {
                                $.growl.error({title: "Delete Failed：", message: response.resultMessage});
                            } else {
                                datatableSchedManage.ajax.reload(null, false);
                            }
                        }
                    });
                }
            },
            className: "bootbox-sm"
        });
    }
    //解析express表达式
    function explainCronExpress(cronExpress) {
        $.ajax({
            "url": "${basePath}/app/scheduler/job/cron/explain",
            "data": {"_method_": "GET", "cronExpress": cronExpress},
            "type": "POST",
            "async": false,
            "error": function () {
            },
            "success": function (response) {
                if (response == null || '0000' != response.resultCode) {
                } else {
                    if (response.result != null) {
                        var nextFireList = response.result.nextFireTimeList;
                        var tbContent = "";
                        if (nextFireList != null && nextFireList.length > 0) {
                            for (var index in nextFireList) {
                                tbContent = tbContent + "<tr><td>" + nextFireList[index] + "</td></tr>";
                            }
                            $("#cron-explain-tb thead span").html("（" + cronExpress + "）");
                            $("#cron-explain-tb tbody").html(tbContent);
                        }

                    }

                }
            }
        });
    }
</script>
<div class="panel-body" style="padding:0;">
    <div class="note note-success" style="padding-bottom: 2px;">
        <ul>
            <h4 class="note-title">Success note title</h4>
            Success note text here.
        </ul>
        <ul>
            <button class="btn btn-success btn-labeled" data-toggle="modal" data-target="#addNewSchedulerDialog">
                <span class="btn-label icon fa fa-plus"></span>添加Scheduler
            </button>
        </ul>
    </div>
<#include "scheduler-job-execpopup.ftl" encoding="UTF-8">
<#include "scheduler-addpopup.ftl" encoding="UTF-8">
<#include "scheduler-editpopup.ftl" encoding="UTF-8">
<#include "scheduler-job-addpopup.ftl" encoding="UTF-8">
<#include "scheduler-job-editpopup.ftl" encoding="UTF-8">
<#include "scheduler-server-checkpopup.ftl" encoding="UTF-8">
<#include "scheduler-job-stopjobpopup.ftl" encoding="UTF-8">

    <div class="table-primary">
        <table id="jq-datatable" cellpadding="0" cellspacing="0" border="0"
               class="table table-striped table-bordered dataTable no-footer"
               aria-describedby="jq-datatables-example_info">
            <thead>
            <tr>
                <th></th>
                <th>计划任务名称</th>
                <th>计划任务描述</th>
                <th>计划任务状态</th>
                <th>所在服务器</th>
                <th>创建时间</th>
                <th>最后更新时间</th>
                <th style="width: 250px;">操作</th>
            </tr>
            </thead>
        </table>
    </div>
</div>


