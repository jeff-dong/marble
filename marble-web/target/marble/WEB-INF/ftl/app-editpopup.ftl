<script>

    init.push(function () {
        $("#edit-marbleversion-div").mask("9.9.9");
        // Add phone validator
        $.validator.addMethod(
                "version_format",
                function(value, element) {
                    return this.optional(element) || /^\d{1}.\d{1}\.\d{1}$/.test(value);
                },
                "Invalid version number."
        );

        $("#jq-validation-app-edit-form").validate({
            ignore: '.ignore, .select2-input',
            focusInvalid: false,
            rules: {
                'jq-validation-appdesc': {
                    maxlength: 100
                },
                'jq-validation-edit-appowner': {
                    required: true,
                    maxlength: 300
                },
                'jq-validation-edit-version':{
                    required: true,
                    version_format: true
                }
            }
        });
    });


    init.push(function () {
        $("#saveNewApp-btn").click(function(){
            var validateResult = $("#jq-validation-app-edit-form").valid();
            if(!validateResult){
                return;
            }
            var appCode = $("#editAppDialog span.edit-appcode").text();
            var appDesc= $("#edit-appdesc-div").val();
            var appOwner = $("#edit-appowner-div")==null?"":$("#edit-appowner-div").val();
            var marVersion = $("#edit-marbleversion-div").val();
            var soaName =  $("#edit-soaservicename-div").val();
            var soaNamespace =  $("#edit-soaservicenamespace-div").val();

            $.ajax({
                "url":"${basePath}/app/edit",
                "data":{"_method_":"patch","code":appCode, "description":appDesc,"owner":appOwner, "marbleVersion":marVersion,"soaServiceName":soaName,"soaServiceNameSpace":soaNamespace},
                "type":"POST",
                "error":function(){
                },
                "success":function(response){
                    if(response == null || '0000' != response.resultCode){
                        $.growl.error({title:"更新失败：", message: response.resultMessage});
                    }else{
                        $.growl.notice({title:"提示：",message: "更新成功！"});
                        $('#editAppDialog').modal("hide");
                        location.reload();
                    }
                }
            });
        });
    });
</script>

<!-- 编辑应用 -->
<div id="editAppDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h4 class="modal-title" id="myModalLabel">编辑应用信息</h4>
        </div>
        <div class="modal-body">
            <div class="alert">
                <button type="button" class="close" data-dismiss="alert">×</button>
                <i class="panel-title-icon fa fa-bullhorn"></i>
                <strong>提示：</strong>更新应用的版本号会: 1) 影响JOB的调度方式；2) 暂停该应用下的所有JOB，需要手动开启
            </div>
            <form class="form-horizontal" id="jq-validation-app-edit-form" novalidate="novalidate">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">应用Code</label>
                        <div class="col-sm-10">
                            <span class="form-control edit-appcode" style="background: #eee;border: none;"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">应用名称</label>
                        <div class="col-sm-10">
                            <span class="form-control edit-appname" style="background: #eee;border: none;"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-appdesc" class="col-sm-2 control-label">应用描述</label>
                        <div class="col-sm-10">
                            <textarea class="form-control" id="edit-appdesc-div" name="jq-validation-appdesc" placeholder="描述下这是个什么鬼..."></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Marble版本号</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="edit-marbleversion-div" name="jq-validation-edit-version" placeholder="输入使用的Marble版本号">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-appowner" class="col-sm-2 control-label">应用拥有者</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="edit-appowner-div" name="jq-validation-edit-appowner"
                                    <#if !Account.hasAdminRole> readonly = "readonly" </#if>
                                   placeholder="拥有者的员工号">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">应用状态</label>
                        <div class="col-sm-10">
                            <span id="edit-appstatus-div" class="form-control" style="background: #eee;border: none;"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-soaservicename" class="col-sm-2 control-label">SOA服务名</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="edit-soaservicename-div" name="jq-validation-edit-soaservicename"
                                   placeholder="SOA服务名">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="jq-validation-soaservicenamespace" class="col-sm-2 control-label">SOA服务NameSpace</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="edit-soaservicenamespace-div" name="jq-validation-edit-soaservicenamespace"
                                   placeholder="SOA服务NameSpace">
                        </div>
                    </div>
                </div>
            </form>
        </div> <!-- / .modal-body -->
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            <button id="saveNewApp-btn" type="button" class="btn btn-primary">保 存</button>
        </div>
    </div>
</div>