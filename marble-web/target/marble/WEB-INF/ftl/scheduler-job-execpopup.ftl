<script>
    function popupAlertTip(dataType, message) {
        $('html,body').animate({scrollTop: 0}, 500);
        setTimeout(function () {
            PixelAdmin.plugins.alerts.clear(
                    true, // animate
                    'pa_page_alerts_default' // namespace
            );
            var options = {
                type: dataType,//$this.attr('data-type'),
                namespace: 'pa_page_alerts_default'
            };
            PixelAdmin.plugins.alerts.add(message, options); //$this.attr('data-text')
        }, 800);
    }

    init.push(function () {

        $("#execute-job-btn").click(function () {
            var appCode = $("#exec-appcode-input").text();
            var schedName = $("#exec-schedName-input").text();
            var jobName = $("#exec-jobName-input").text();
            var jobParam = $("#exec-jobParam-input").val();
            var serverIp = $("#exec-serverIP-input").val();
            var serverPort = $("#exec-serverPort-input").val();

            var type = $("#exec-frame-select").val();

            if (appCode.length == 0 || schedName.length == 0 || jobName.length == 0 || serverIp.length == 0 || serverPort.length == 0) {
                alert("参数不合法，请检查");

                return;
            }

            $.ajax({
                "url": "${basePath}/app/scheduler/job/execute",
                "async": false,
                "data": {
                    "_method_": "post",
                    "appCode": appCode,
                    "schedName": schedName,
                    "jobName": jobName,
                    "serverIp": serverIp,
                    "serverPort": serverPort,
                    "jobParam": jobParam,
                    "type": type

                },
                "type": "POST",
                "error": function () {
                    $.growl.error({title: "操作失败：", message: "未知错误，请刷新页面后重试"});
                },
                "success": function (response) {
                    if (response != null && '0000' != response.resultCode) {
                        $.growl.error({title: "执行失败：", message: response.resultMessage});
                    } else {
                        $("#execSchedJobDialog").modal("hide");
                        popupAlertTip("success", " 执行请求提交成功，请通过 [执行日志查询] TAB页面查看具体执行结果。");
                    }
                }
            });

        });
    });

</script>
<!-- JOB执行popup-->
<div id="execSchedJobDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog" style="width:800px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">手动执行JOB</h4>
            </div>
            <div class="modal-body">
                <div>
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="control-label" for="inputDefault-4">应用CODE</label>
                            <span class="form-control" id="exec-appcode-input"
                                  style="color: #1C83C5;font-weight: bold;"></span>

                            <p class="help-block">JOB对应的 [AppCode]</p>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="inputDefault-4">计划任务Name</label>
                            <span class="form-control" id="exec-schedName-input"
                                  style="color: #1C83C5;font-weight: bold;"></span>

                            <p class="help-block">JOB对应的[计划任务名称]</p>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="inputDefault-4">JOB名称</label>
                            <span class="form-control" id="exec-jobName-input"
                                  style="color: #1C83C5;font-weight: bold;"></span>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="inputDefault-4">JOB执行参数 <span
                                    style="color: #DE8D8D;"></span></label>
                            <input type="text" class="form-control" id="exec-jobParam-input"/>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="inputDefault-4">服务器IP+端口号 <span style="color: #DE8D8D;">(*必填)</span></label></br>
                            <input type="text" class="form-control" id="exec-serverIP-input"
                                   style="width:300px;display: inline-block;"/>:
                            <input type="text" class="form-control" id="exec-serverPort-input"
                                   style="width:100px;display: inline-block;"/>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="inputDefault-4">执行框架选择</label>
                            <select class="form-control form-group-margin" id="exec-frame-select">
                                <option value="NETTY">Netty</option>
                                <option value="THRIFT">Thrift</option>
                            </select>
                        </div>

                    </div>
                </div>
            </div>
            <!-- / .modal-body -->
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button id="execute-job-btn" type="button" class="btn btn-primary">执行一次</button>
            </div>
        </div>
    </div>
</div>
