<script>

    init.push(function () {
        $("#marbleversion-div").mask("9.9.9");
        // Add phone validator
        $.validator.addMethod(
                "version_format",
                function(value, element) {
                    var check = false;
                    return this.optional(element) || /^\d{1}.\d{1}\.\d{1}$/.test(value);
                },
                "Invalid version number."
        );

        // Setup validation
        $("#jq-validation-app-form").validate({
            ignore: '.ignore, .select2-input',
            focusInvalid: false,
            rules: {
                'jq-validation-appcode': {
                    required: true,
                    number:true,
                    maxlength: 10
                },
                'jq-validation-appname': {
                    required: true,
                    maxlength: 30
                },
                'jq-validation-appdesc': {
                    required: true,
                    maxlength: 100
                },
                'jq-validation-appowner': {
                    required: true,
                    maxlength: 6
                },
                'jq-validation-version':{
                    required: true,
                    version_format: true
                }
            }
        });
    });


    init.push(function () {
        $("#addNewApp-btn").click(function(){
            var validateResult = $("#jq-validation-app-form").valid();
            if(!validateResult){
                return;
            }

            var appCode = $("#appcode-div").val();
            var appName = $("#appname-div").val();
            var appDesc = $("#appdesc-div").val();
            var appOwner = $("#appowner-div").val();
            var marVersion = $("#marbleversion-div").val();

            $.ajax({
                "url":"${basePath}/app/add",
                "data":{"_method_":"patch","code":appCode,"name":appName, "description":appDesc,"owner":appOwner, "marbleVersion":marVersion},
                "type":"POST",
                "error":function(){
                },
                "success":function(response){
                    if(response == null || '0000' != response.resultCode){
                        $.growl.error({title:"添加失败：", message: response.resultMessage});
                    }else{
                        $.growl.notice({title:"提示：",message: "操作成功！"});
                        $('#addNewAppDialog').modal("hide");
                        location.reload();
                    }
                }
            });
        });
    });
</script>
<!-- 添加应用 -->
<div id="addNewAppDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h4 class="modal-title" id="myModalLabel">添加新应用</h4>
        </div>
        <div class="modal-body">
            <form class="form-horizontal" id="jq-validation-app-form" novalidate="novalidate">
                <div class="panel-body">
                    <div class="form-group">
                        <label for="jq-validation-appcode" class="col-sm-2 control-label">应用Code</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="appcode-div" name="jq-validation-appcode" placeholder="AppCode">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-appname" class="col-sm-2 control-label">应用名称</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="appname-div" name="jq-validation-appname" placeholder="应用名称">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-appdesc" class="col-sm-2 control-label">应用描述</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" id="appdesc-div" name="jq-validation-appdesc" placeholder="描述下这是个什么鬼..."></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-appowner" class="col-sm-2 control-label">应用拥有者</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="appowner-div" name="jq-validation-appowner" placeholder="拥有者的员工号">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Marble版本号</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="marbleversion-div" name="jq-validation-version" placeholder="输入使用的Marble版本号">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">应用状态</label>
                        <div class="col-sm-10">
                            <span type="text" class="form-control" style="background: #eee;border: none;">初始状态（可用）</span>
                        </div>
                    </div>
                </div>
            </form>
        </div> <!-- / .modal-body -->
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            <button id="addNewApp-btn" type="button" class="btn btn-primary">保 存</button>
        </div>
    </div>
</div>