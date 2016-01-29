<script>

    init.push(function () {
        // Setup validation
        $("#jq-validation-server-form").validate({
            ignore: '.ignore, .select2-input',
            focusInvalid: false,
            rules: {
                'jq-validation-appcode': {
                    required: true,
                    number:true
                },
                'jq-validation-servergroup': {
                    required: true,
                    maxlength: 30
                },
                'jq-validation-servername': {
                    required: true,
                    maxlength: 30
                },
                'jq-validation-serverip': {
                    required: true
                },
                'jq-validation-serverdesc': {
                    maxlength: 100
                }
            }
        });
    });


    init.push(function () {
        $("#addNewAppServer-btn").click(function(){
            var validateResult = $("#jq-validation-server-form").valid();
            if(!validateResult){
                return;
            }

            var appCode = $("#jq-validation-server-form .app-code").text();
            var servergroup = $("#servergroup-div").val();
            var servername = $("#servername-div").val();
            var serverip = $("#serverip-div").val();
            var serverdesc = $("#serverdesc-div").val();

            $.ajax({
                "url":"${basePath}/app/server/add",
                "data":{"_method_":"patch","appCode":appCode,"group":servergroup, "name":servername, "ip":serverip , "description":serverdesc},
                "type":"POST",
                "error":function(){
                },
                "success":function(response){
                    if(response == null || '0000' != response.resultCode){
                        $.growl.error({title:"添加失败：", message: response.resultMessage});
                    }else{
                        $.growl.notice({title:"提示：",message: "操作成功！"});
                        $('#addNewAppServerDialog').modal("hide");
                        location.reload();
                    }
                }
            });
        });
    });
</script>
<div id="addAppserverDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h4 class="modal-title" id="myModalLabel">为应用添加Server</h4>
        </div>
        <div class="modal-body">
            <form class="form-horizontal" id="jq-validation-server-form" novalidate="novalidate">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">应用Code</label>
                        <div class="col-sm-10">
                            <span class="form-control app-code" style="background: #eee;border: none;"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-servergroup" class="col-sm-2 control-label">服务器组</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="servergroup-div" name="jq-validation-servergroup" placeholder="服务器组" value="DEFAULT">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-servername" class="col-sm-2 control-label">服务器名称</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="servername-div" name="jq-validation-servername" placeholder="服务器名称"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-serverip" class="col-sm-2 control-label">服务器IP</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="serverip-div" name="jq-validation-serverip" placeholder="服务器IP">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-serverdesc" class="col-sm-2 control-label">服务器描述</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" id="serverdesc-div" name="jq-validation-serverdesc" placeholder="服务器描述信息"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">服务器状态</label>
                        <div class="col-sm-10">
                            <span type="text" class="form-control" style="background: #eee;border: none;">初始状态（可用）</span>
                        </div>
                    </div>
                </div>
            </form>
        </div> <!-- / .modal-body -->
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            <button id="addNewAppServer-btn" type="button" class="btn btn-primary">保 存</button>
        </div>
    </div>
    </div>
</div>