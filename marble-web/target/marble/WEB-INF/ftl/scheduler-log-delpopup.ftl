<script>

    init.push(function () {
        $("#logHistoryDel-btn").click(function (data) {//
            var appCode = '${appCode}';
            var schedName = $("#log-del-sched-name").val();
            var jobName = $("#log-del-job-name").val();
            var delProcessing = $("#delete-processing-log-cb").prop("checked");
            if (appCode == null || appCode.length == 0) {
                alert("应用Code不能为空");
                return false;
            }
            $.ajax({
                "url": "${basePath}/log/job/delete",
                "data": {"_method_": "DELETE", "appCode": appCode, "schedName": schedName, "jobName": jobName, "delProcessing":delProcessing},
                "type": "POST",
                "error": function () {
                },
                "success": function (response) {
                    if (response == null || '0000' != response.resultCode) {
                        $.growl.error({title: "删除失败：", message: response.resultMessage});
                    } else {
                        $.growl.notice({title: "提示：", message: "删除成功！"});
                        $('#appLogDeleteDialog').modal("hide");
                        location.reload();
                    }
                }
            });
        });
    });

</script>
<div id="appLogDeleteDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">删除任务日志</h4>
            </div>
            <div class="modal-body">
                <div class="alert" style="color: red;">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <i class="panel-title-icon fa fa-bullhorn"></i>
                    <strong>风险提示：</strong>同步JOB的【请求中】的日志被删除可能会导致同步互斥判断失效, 如不确定建议不要勾选"删除'处理中'日志"
                </div>
                <form class="form-horizontal" id="jq-validation-log-del-form" novalidate="novalidate">
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="col-sm-4 control-label">应用Code</label>
                            <div class="col-sm-8">
                                <span class="form-control add-appcode"
                                      style="background: #eee;border: none;">${appCode}</span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">计划任务名称</label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" id="log-del-sched-name"
                                       name="log-del-sched-name" placeholder="输入要删除的计划任务名称">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="jq-validation-jobname" class="col-sm-4 control-label">Job名称</label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" id="log-del-job-name" name="log-del-job-name"
                                       placeholder="输入要删除的JOB名称">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">删除'处理中'日志</label>
                            <div class="col-sm-8">
                                <div class="radio">
                                    <label>
                                        <input type="checkbox" id="delete-processing-log-cb" value="2" class="px">
                                        <span class="lbl">&nbsp;</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                </form>
            </div>
            <!-- / .modal-body -->
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button id="logHistoryDel-btn" type="button" class="btn btn-primary">提交删除</button>
            </div>
        </div>
    </div>
</div>
