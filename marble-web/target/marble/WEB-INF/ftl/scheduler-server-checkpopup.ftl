
<script>
    init.push(function () {
        $("#server-check-health-btn").click(function(){
            $("#server-list-tb tr").each(function(trindex, tritem){
                var hostIp = $(tritem).children('td').eq(1).text();
                var hostPort = $(tritem).children('td').eq(2).text();
                var resultTd = $(tritem).children('td').eq(3);
                if(hostIp == null || hostPort == null || hostPort <=0){
                    resultTd.html('<i class="panel-title-icon fa fa-minus-circle" style="color:red;"></i>');
                }else{
                    $.ajax({
                        "url": "${basePath}/app/scheduler/server/checkhealth",
                        "data":{"_method_":"get","hostIp":hostIp,"port":hostPort},
                        "type":"POST",
                        "error": function () {
                        },
                        "success": function (response) {
                            if(response != null){
                                if ('0000' == response.resultCode) {
                                    resultTd.html('<i class="panel-title-icon fa fa-check-circle" style="color:green;"></i>');
                                }else{
                                    resultTd.html('<i class="panel-title-icon fa fa-minus-circle" style="color:red;"></i>');
                                }
                            }else{
                                resultTd.html('未知');
                            }
                        }
                    });
                }
            });
        });
    });

</script>

<!-- 更新Job -->
<div id="serverCheckHealthDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">服务器连通性测试</h4>
            </div>
            <div class="modal-body">
                <div class="alert">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <i class="panel-title-icon fa fa-bullhorn"></i>
                    <strong>提示：</strong>测试结果仅供参考！
                </div>
                <div class="main-content">
                        <div class="panel panel-dark panel-light-green">
                            <div class="panel-heading">
                                <span class="panel-title"><i class="panel-title-icon fa fa-laptop"></i>计划任务所在服务器列表</span>
                            </div> <!-- / .panel-heading -->
                            <table id="server-list-tb" class="table">
                                <thead>
                                <tr>
                                    <th>计划任务名称</th>
                                    <th>服务器IP</th>
                                    <th>服务器端口号</th>
                                    <th>结果</th>
                                </tr>
                                </thead>
                                <tbody class="valign-middle">
                                    <tr>
                                        <td colspan="4"><span style="color: #989494;font-size: 13px;">在当前计划任务下，没有查询到任何服务器信息！</span></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div> <!-- / .panel -->
                </div>
            </div>
            <!-- / .modal-body -->
            <div class="modal-footer">
                <button id="server-check-health-btn" type="button" class="btn btn-primary" style="width:100%;background-image: linear-gradient(to bottom,#1e8cd3 0,#45A6E6 100%);">开始检查</button>
            </div>
        </div>
    </div>
</div>


