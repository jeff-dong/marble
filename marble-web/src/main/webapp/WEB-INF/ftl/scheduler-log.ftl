
<div class="row">
    <script>
        var joblogDT;
        init.push(function () {
            $('#bs-datepicker-range').datepicker( {
                format: "yyyy-mm-dd",
                autoclose: true,
                todayBtn: 'linked',
                language: 'zh-CN'
            });

            joblogDT = $('#jq-datatable-joblog').DataTable({
                "processing": true,
                "serverSide": true,
                "autoWidth": false,
                "paging":true,
                "ajax": {
                    "url": "${basePath}/log/job/query",
                    "dataSrc": "result",
                    "type":"POST",
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
                        if(beginDate != null && beginDate.length>0){
                            data.beginDate = beginDate;
                        }
                        if(endDate != null && endDate.length>0){
                            data.endDate = endDate;
                        }
                        var statusCode = $("#statusCode-select").val();
                        if(statusCode != null && statusCode>0){
                            data.resultCode = statusCode;
                        }
                        return "_method_=get&jsonParam=" + JSON.stringify(data);
                    }
                },
                "columns": [
                    {"data": "appCode"},
                    {"data": "schedName"},
                    {"data": "jobName"},
                    {"data": "jobCronExpress","orderable": false},
                    {"data": "serverInfo"},
                    {"data": function(row){
                        return row.statusDesc;
                    }},
                    {"data": "resultMsg","orderable": false},
                    {"data": "createTime"}
                ],
                "initComplete": function (settings, json) {
                    $('#jq-datatable-joblog_wrapper .table-caption').text('日志列表');
                    $('#jq-datatable-joblog_wrapper .dataTables_filter input').attr('placeholder', '输入ID查找');
                },
                "order": [[7, 'desc']]
            });
            $("#searchJobLogQty-btn").click(function () {
                joblogDT.draw(false);
            });
        });
        function clearQueryAreaForLog(){
            $("#schedname_input").val("");
            $("#jobname_input").val("");
        }
    </script>

    <div class="panel-tt">
        <div class="form-horizontal">
            <div class="panel-body" style="background:#eee;">
                <div class="row">
                    <div class="col-md-2">
                        <input type="text" name="name" id="schedname_input" placeholder="输入【计划任务名称】" class="form-control form-group-margin">
                    </div>
                    <div class="col-md-2">
                        <input type="text" name="name" id="jobname_input" placeholder="输入【Job名称】" class="form-control form-group-margin">
                    </div>
                    <div class="col-md-4">
                        <div class="input-daterange input-group" id="bs-datepicker-range" style="right: 220px;left: 20px;max-width: 800px;">
                            <input type="text" class="input-sm form-control" name="start" placeholder="开始日期">
                            <span class="input-group-addon">to</span>
                            <input type="text" class="input-sm form-control" name="end" placeholder="结束日期">
                        </div>
                    </div>
                    <div class="col-md-2" style="margin-left:10px;">
                        <select id="statusCode-select" class="form-control" tabindex="-1" style="height:30px;">
                            <option selected="">全部</option>
                            <option value="10">成功</option>
                            <option value="20">失败</option>
                        </select>
                    </div>
                </div><!-- row -->
            </div>
            <div class="panel-footer text-right">
                <button class="btn btn-outline btn-labeled btn-danger" onclick="clearQueryAreaForLog();" style="margin-right:40px;">
                    <span class="btn-label icon fa fa-trash-o"></span>清空
                </button>
                <button  id="searchJobLogQty-btn" class="btn btn-primary btn-labeled">
                    <span class="btn-label icon fa fa-search"></span>日志查询
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
                    <th>Cron表达式</th>
                    <th>执行的服务器</th>
                    <th>执行结果</th>
                    <th>结果描述</th>
                    <th style="width:150px;">执行时间</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>