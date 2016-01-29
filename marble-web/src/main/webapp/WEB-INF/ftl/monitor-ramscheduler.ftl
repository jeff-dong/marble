<style type="text/css">
    table.panel-collapse thead{
        font-weight: bolder;
    }
    .note-info{
        font-weight: bolder;
    }
    .accordion-toggle{
        font-weight: bolder;
        font-size:15px;
    }
</style>
<script>
    var init = [];
    init.push(function () {
       // populateRamSchedTable();
        //获取所有ram中的计划任务信息
        function populateRamSchedTable() {
            $.ajax({
                "url": "${basePath}/app/scheduler/cache/query",
                "async": true,
                "data": {"_method_": "get"},
                "type": "POST",
                "error": function () {
                    $.growl.error({title: "查询失败：", message: "未知错误"});
                },
                "success": function (response) {
                    if (response != null && '0000' == response.resultCode) {
                        var result = response.result;
                        if (result != null && result.length>0) {
                            var ramSchedTable = $("#ramsched-accordion");
                            ramSchedTable.empty();
                            for (var index in result) {
                                var appCon = result[index].appDetail;
                                var panelCon = '<div class="panel">';
                                panelCon = panelCon + '<div class="panel-heading">';
                                panelCon = panelCon + '<a class="accordion-toggle" data-toggle="collapse" data-parent="#ramsched-accordion" href="#collapse-' + index + '">'
                                                        +appCon.name+' -> '+result[index].name+' | '+result[index].description+
                                                      '</a>';
                                panelCon = panelCon + '</div>';
                                panelCon = panelCon + '<div id="collapse-' + index + '" class="panel-collapse collapse" style="height: auto;">';
                                panelCon = panelCon + '<div class="panel-body">';

                                //应用相关
                                if(appCon != null){
                                    panelCon = panelCon + '<p style="margin: 0px 0 20px 0;">应用: '+appCon.code+' | '+appCon.name+' | '+appCon.description+' | '+appCon.owner+' | '+appCon.statusDesc+'</p>';
                                }
                                //服务器相关
                                var serverCon = result[index].serverDetails;
                                if(serverCon != null && serverCon.length>0){
                                    var serverDiv = '';
                                    for(var indexS in serverCon){
                                        serverDiv = serverDiv + '<tr><td>'+serverCon[indexS].ip+'</td><td>'+serverCon[indexS].port+'</td></tr>';
                                    }
                                    panelCon = panelCon + '<div class="note note-info">服务器信息</div>'+
                                    '<table class="table panel-collapse in"><thead><td>IP地址</td><td>端口号</td></thead> <tbody>'+serverDiv+'</tbody></table>';

                                }
                                //Job相关
                                var jobCon = result[index].jobs;
                                if(jobCon != null && jobCon.length>0){
                                    var jobDiv = '';
                                    for(var indexJ in jobCon){
                                        jobDiv = jobDiv + '<tr>' +
                                        '<td>'+jobCon[indexJ].name+'</td>' +
                                        '<td>'+jobCon[indexJ].cronExpress+'</td>' +
                                        '<td>'+jobCon[indexJ].startTime+'</td>' +
                                        '<td>'+jobCon[indexJ].endTime+'</td>' +
                                        '<td>'+jobCon[indexJ].nextFireTime+'</td>' +
                                        '</tr>';
                                    }
                                    panelCon = panelCon + '<div class="note note-info">Job信息</div>'+
                                    '<table class="table panel-collapse in"><thead><td>名称</td><td>Cron表达式</td><td>开始时间</td><td>结束时间</td><td>下次触发时间</td></thead> <tbody>'+jobDiv+'</tbody></table>';
                                }

                                panelCon = panelCon + '</div></div>';
                                panelCon = panelCon + '</div>';
                                ramSchedTable.append(panelCon);
                            }
                        }
                    }
                }
            });
        }
    });
</script>

<div class="panel-body" style="padding:0;">
    <!-- / Light table -->
    <div class="panel-group panel-group-success" id="ramsched-accordion">
        <p class="text-light-gray" style="font-style: italic;color: #908A8A;font-size: 15px;">没有查询到任何缓存信息</p>
    </div>
</div>