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

        $("#stop-job-btn").click(function () {
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
<div id="stopSchedJobDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">手动线程中断</h4>
            </div>
            <div class="modal-body">
                <div class="alert">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <i class="panel-title-icon fa fa-bullhorn"></i>
                    <strong>线程成功中断必要条件：</strong>
                    <ul>
                        <li>JOB的调度已经被暂停；</li>
                        <li>代码中的Marble-Agent版本更新到2.3.0及以上；</li>
                        <li>JOB的execute(或executeSync)中未“吃掉” InterruptedException异常；</li>
                    </ul>
                </div>
                <div class="main-content">
                    <div class="panel panel-dark panel-light-green">
                        <div class="panel-heading">
                            <span class="panel-title"><i class="panel-title-icon fa fa-laptop"></i>JOB所在服务器列表</span>
                        </div> <!-- / .panel-heading -->
                        <table id="job-server-list-tb" class="table">
                            <thead>
                            <tr>
                                <th>JOB名称</th>
                                <th>服务器IP</th>
                                <th>服务器端口号</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody class="valign-middle">
                            <tr>
                                <td colspan="4"><span
                                        style="color: #989494;font-size: 13px;">在当前JOB下，没有查询到任何服务器信息！</span></td>
                            </tr>
                            </tbody>
                        </table>
                    </div> <!-- / .panel -->
                </div>
            </div>
            <!-- / .modal-body -->
            <div class="modal-footer">

            </div>
        </div>
    </div>
</div>
