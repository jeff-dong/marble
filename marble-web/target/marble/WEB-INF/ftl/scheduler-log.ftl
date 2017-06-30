<style type="text/css">
    .DT-search, .DT-search input {
        width: 330px !important;
    }
    #jq-datatable-joblog td{
        max-width:150px;
    }
</style>
<div class="row">
    <script>
        var joblogDT;
        init.push(function () {
            $('#bs-datepicker-range').datepicker({
                format: "yyyy-mm-dd",
                autoclose: true,
                todayBtn: 'linked',
                language: 'zh-CN'
            });

            joblogDT = $('#jq-datatable-joblog').DataTable({
                "processing": true,
                "serverSide": true,
                "autoWidth": false,
                "paging": true,
                "ajax": {
                    "url": "${basePath}/log/job/query",
                    "dataSrc": "result",
                    "type": "POST",
                    "data": function (data) {
                        data.appCode = $("#sched-panel-joblog").attr("appcode");
                        data.schedName = $("#schedname_input").val();
                        data.jobName = $("#jobname_input").val();
                        var beginDate = $('#bs-datepicker-range input[name="start"]').val();
                        var endDate = $('#bs-datepicker-range input[name="end"]').val();
                        if (endDate < beginDate) {
                            $.growl.error({title: "OPPS!", message: '[开始时间] 必须要小于 [结束时间]'});
                            return false;
                        }
                        if (beginDate != null && beginDate.length > 0) {
                            data.beginDate = beginDate;
                        }
                        if (endDate != null && endDate.length > 0) {
                            data.endDate = endDate;
                        }
                        var reqStatusCode = $("#reqStatusCode-select").val();
                        if (reqStatusCode != null && reqStatusCode > 0) {
                            data.reqResultCode = reqStatusCode;
                        }
                        var execStatusCode = $("#execStatusCode-select").val();
                        if (execStatusCode != null && execStatusCode > 0) {
                            data.execResultCode = execStatusCode;
                        }
                        return "_method_=get&jsonParam=" + JSON.stringify(data);
                    }
                },
                "columns": [
                    {"data": "appCode"},
                    {"data": "schedName"},
                    {"data": "jobName"},
                    {"data": "requestNo"},
                    {"data": "jobCronExpress", "orderable": false},
                    {"data": "serverInfo"},
                    {"data": "otherInfo"},
                    {
                        "data": function (row) {
                            var tempRes = row.reqResultCode + ":" + row.reqResultMsg;
                            var cssStyle = "";
                            switch (row.reqResultCode) {
                                case 10:
                                    cssStyle = "color:#2175AC;";
                                    break;
                                case 0:
                                    break;
                                default:
                                    cssStyle = "color:red;font-weight: bold;";
                                    break;
                            }
                            return "<span style='" + cssStyle + "'>" + tempRes + "</span>";
                        }, "orderable": false
                    },

                    {
                        "data": function (row) {
                            var cssStyle = "";
                            switch (row.execResultCode) {
                                case 10:
                                    cssStyle = "color:#2175AC;";
                                    break;
                                case 0:
                                    break;
                                default:
                                    cssStyle = "color:red;font-weight: bold;";
                                    break;
                            }
                            return '<a href="#"  style="' + cssStyle + '" ' +
                                    'data-url="${basePath}/log/job/execStatus/update" data-type="text" data-pk="'+row.requestNo+'" data-title="输入修改后的值" ' +
                                    'class="editableColmn editable editable-click">'+ row.execResultCode + '</a><span style="' + cssStyle + '">:' + safeString(row.execResultMsg,'N/A') + '</span>';
                        }, "orderable": false
                    },

                    {
                        "data": function (row) {
                            return safeString(row.beginTime, " N/A");
                        }
                    },
                    {
                        "data": function (row) {
                            return safeString(row.endTime, "N/A");
                        }
                    },
                    {"data": "consumingTime"}

                ],
                "drawCallback": function (settings) {
                    $('#jq-datatable-joblog a.editableColmn').editable({
                        params: function (params) {
                            params._method_ = "PATCH";
                            return params;
                        },
                        success: function (response) {
                            if (response != null && '0000' == response.resultCode) {
                                $.growl.notice({title: "提示：", message: "更新成功！"});
                            } else {
                                $.growl.error({title: "更新失败：", message: response.resultMessage});
                            }
                        }
                    });
                },
                "initComplete": function (settings, json) {
                    $('#jq-datatable-joblog_wrapper .table-caption').text('日志列表');
                    $('#jq-datatable-joblog_wrapper .dataTables_filter input').attr('placeholder', '输入流水号查找');
                },
                "order": [[9, 'desc']]
            });
            $("#searchJobLogQty-btn").click(function () {
                joblogDT.draw(true);
            });
        });
        function clearQueryAreaForLog() {
            $("#schedname_input").val("");
            $("#jobname_input").val("");
        }

        //清空日志记录
        function clearLogHistory(obj,appCode){
            $("#jq-validation-job-form span.add-schedname").html(appCode);
            $("#appLogDeleteDialog").modal();
        }

        function clearLogs() {
            /*
            bootbox.confirm({
                message: "确定要删除应用【"+key+"】下的所有日志记录？",
                callback: function(result) {
                    if(result){
                        $.ajax({
                            "url":"${basePath}/log/job/delete?",
                            "type":"delete",
                            "error":function(){
                            },
                            "success":function(response){
                                if(response != null && '0000' != response.resultCode){
                                    $.growl.error({title:"Update Failed：", message: response.resultMessage});
                                }else{
                                    location.reload();
                                }
                            }
                        });
                    }
                },
                className: "bootbox-sm"
            });
            */
        }
    </script>

    <div class="panel-tt">
        <div class="form-horizontal">
            <div class="panel-body" style="background:#eee;">
                <div class="row">
                    <div class="col-md-2">
                        <input type="text" name="name" id="schedname_input" placeholder="输入【计划任务名称】"
                               class="form-control form-group-margin">
                    </div>
                    <div class="col-md-2">
                        <input type="text" name="name" id="jobname_input" placeholder="输入【Job名称】"
                               class="form-control form-group-margin">
                    </div>
                    <div class="col-md-3">
                        <div class="input-daterange input-group" id="bs-datepicker-range"
                             style="right: 220px;left: 20px;max-width: 800px;">
                            <input type="text" class="input-sm form-control" name="start" placeholder="开始日期">
                            <span class="input-group-addon">to</span>
                            <input type="text" class="input-sm form-control" name="end" placeholder="结束日期">
                        </div>
                    </div>
                    <div class="col-md-2" style="margin-left:10px;">
                        <select id="reqStatusCode-select" class="form-control" tabindex="-1" style="height:30px;">
                            <option selected="">[请求状态] 全部</option>
                            <option value="10">[请求状态] 请求中</option>
                            <option value="0">[请求状态] 成功</option>
                            <option value="20">[请求状态] 失败</option>
                        </select>
                    </div>
                    <div class="col-md-2" style="margin-left:10px;">
                        <select id="execStatusCode-select" class="form-control" tabindex="-1" style="height:30px;">
                            <option selected="">[执行状态] 全部</option>
                            <option value="10">[执行状态] 请求中</option>
                            <option value="0">[执行状态] 成功</option>
                            <option value="20">[执行状态] 失败</option>
                        </select>
                    </div>
                </div>
                <!-- row -->
            </div>
            <div class="panel-footer text-right">
            <#--<button class="btn btn-outline btn-labeled btn-danger" onclick="clearLogs();" style="margin-right:40px;">-->
            <#--<span class="btn-label icon fa fa-trash-o"></span>清空日志-->
            <#--</button>-->
                <button class="btn btn-outline btn-labeled btn-danger" onclick="clearQueryAreaForLog();"
                        style="margin-right:20px;">
                    清空查询
                </button>
                <button id="searchJobLogQty-btn" class="btn btn-primary btn-labeled" style="margin-right:20px;">
                    <span class="btn-label icon fa fa-search"></span>日志查询
                </button>
                <button class="btn btn-outline btn-labeled btn-danger" onclick="clearLogHistory(this, '${APP_CODE}');"
                        style="margin-right:40px;">
                    清空日志
                </button>
            </div>
        </div>
    </div>
    <div class="panel-body" style="padding-bottom:0; background: none;">
        <div class="table-primary">
            <table id="jq-datatable-joblog" cellpadding="0" cellspacing="0" border="0"
                   class="table table-striped table-bordered dataTable no-footer"
                   aria-describedby="jq-datatables-example_info">
                <thead>
                <tr>
                    <th>应用</th>
                    <th>计划任务名称</th>
                    <th>Job名称</th>
                    <th style="width: 100px;word-break:break-all;">流水号</th>
                    <th style="width: 100px">Cron表达式</th>
                    <th>执行的服务器</th>
                    <th style="width: 100px;">其它信息</th>
                    <th style="width: 60px;">请求结果</th>
                    <th>执行结果</th>
                    <th style="width:70px;">开始时间</th>
                    <th style="width:70px;">结束时间</th>
                    <th style="width:30px;">耗时</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
<#include "scheduler-log-delpopup.ftl" encoding="UTF-8">
</div>