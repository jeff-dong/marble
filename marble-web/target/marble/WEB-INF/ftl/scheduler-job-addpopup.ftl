<style type="text/css">

    #CronGenTabs > li >a{
        padding:5px 6px 5px 6px;
        margin-right:3px;
    }
    #CronGenTabs > li{
        font-weight: bolder;
        width: 60px;
        text-align: center;
    }
    #CronGenTabs > li.active{
        font-weight: bolder;
    }
    #CronGenMainDiv{
        font-size:10px;
    }
</style>
<script>

    init.push(function () {
        //add job validation
        $("#jq-validation-job-form").validate({
            ignore: '.ignore, .select2-input',
            focusInvalid: false,
            rules: {
                'jq-validation-jobname': {
                    required: true,
                    maxlength: 50
                },
                'jq-validation-jobdesc': {
                    maxlength: 1000
                },
                'jq-validation-jobcron': {
                    required: true,
                    maxlength: 50
                },
                'jq-validation-jobparam': {
                    maxlength: 1000
                }
            }
        });

    });

    init.push(function () {
        var jobSyncInput = $(".job-sync-call-input");
        jobSyncInput.tooltip();

        var jobCallTypeRadioBtn = $("input[type=radio][name='job-call-type-radio']");

        $("#jobcron-div").cronGen();
        //添加新Job
        $("#addNewJob-btn").click(function () {
            var validateResult = $("#jq-validation-job-form").valid();
            if (!validateResult) {
                return;
            }

            var schedName = $("#jq-validation-job-form .add-schedname").text();
            var jobName = $("#jobname-div").val();
            var jobDesc = $("#jobdesc-div").val();
            var jobCron = $("#jobcron-div").val();
            var jobParam = $("#jobparam-div").val();
            var jobMisFire = $('input[name="jobms-radio"]:checked').val();
            //最大等待时间
            var jobMaxWaitTime = jobSyncInput.val();
            //JOB调用方式
            var jobCallType = jobCallTypeRadioBtn.filter(':checked').val();

            $.ajax({
                "url": "${basePath}/app/scheduler/job/add",
                "data":{"_method_":"patch",
                    "app.code":${appCode},
                    "scheduler.name":schedName,
                    "name":jobName,
                    "description":jobDesc,
                    "cronExpress":jobCron,
                    "param":jobParam,
                    "misfireStrategy":jobMisFire,
                    "isSynchronous":(jobCallType=="SYNC"),
                    "maxWaitTime":jobMaxWaitTime
                },
                "type": "POST",
                "error": function () {
                },
                "success": function (response) {
                    if (response == null || '0000' != response.resultCode) {
                        $.growl.error({title: "添加失败：", message: response.resultMessage});
                    } else {
                        $.growl.notice({title: "提示：", message: "操作成功！"});
                        $('#addNewSchedulerDialog').modal("hide");
                        location.reload();
                    }
                }
            });
        });

        //JOB调度方式选择
        jobCallTypeRadioBtn.click(function(){
            var jobCallType = $(this).val();
            jobSyncInput.val(null);

            switch(jobCallType){
                case "SYNC":
                    jobSyncInput.rules("add", { required: true, messages: { required: "请输入最大等待时长(分钟)"} });
                    jobSyncInput.css("display","inline"); break;
                case "ASYNC":
                    jobSyncInput.rules("remove", "required");
                    jobSyncInput.css("display","none"); break;
                default: return;
            }
        });
    });
</script>
<!-- 添加 Job-->
<div id="addNewJobDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog" style="width:800px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">添加新Job</h4>
            </div>
            <div class="modal-body">
                <div class="alert" style="color: red;">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <i class="panel-title-icon fa fa-bullhorn"></i>
                    <strong>提示：</strong>JOB【同步调用方式】暂时未经过生产真实数据验证，选择请谨慎！
                </div>
                <form class="form-horizontal" id="jq-validation-job-form" novalidate="novalidate">
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
                                <span class="form-control add-schedname"style="background: #eee;border: none;"></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="jq-validation-jobname" class="col-sm-4 control-label">Job名称</label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" id="jobname-div" name="jq-validation-jobname" placeholder="Job名称（最大50个字符）">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="jq-validation-jobdesc" class="col-sm-4 control-label">Job描述</label>
                            <div class="col-sm-8">
                                <textarea class="form-control" id="jobdesc-div" name="jq-validation-jobdesc" placeholder="描述下这是个什么鬼...（最大1000个字符）"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="jq-validation-jobcron" class="col-sm-4 control-label">Cron表达式</label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" id="jobcron-div" placeholder="Cron表达式">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="jq-validation-jobcron" class="col-sm-4 control-label">JOB调用方式(同步/异步)</label>
                            <div class="col-sm-8">
                                <p>
                                    <label class="radio">
                                        <input type="radio" name="job-call-type-radio" class="px" checked="checked" value="ASYNC">
                                        <span class="lbl">异步调用</span>
                                    </label>

                                    <label class="radio" style="display:inline";>
                                        <input type="radio" name="job-call-type-radio" class="px" value="SYNC">
                                        <span class="lbl">同步调用</span>
                                    </label>
                                    <input class="job-sync-call-input tooltip-warning" style="display:none;width: 220px;margin-left:15px;padding-left:8px;" placeholder="输入最大等待时间 (分钟)" data-original-title="提示：系统将尝试在 最大等待时间内等待JOB返回结果，否则返回超时"/> &nbsp;
                                </p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="vad-job-edit-jobdesc" class="col-sm-4 control-label">MisFire策略</label>

                            <div class="col-sm-8">
                                <div class="radio" style="margin-top: 0;">
                                    <label>
                                        <input type="radio" name="jobms-radio" value="2" checked="">
                                        不触发立即执行(等待下次Cron触发频率到达时刻开始按照Cron频率依次执行)
                                    </label>
                                </div>
                                <div class="radio" style="margin-top: 0;">
                                    <label>
                                        <input type="radio" name="jobms-radio" value="1">
                                        以当前时间为触发频率立刻触发一次执行
                                    </label>
                                </div>
                                <div class="radio" style="margin-bottom: 0;">
                                    <label>
                                        <input type="radio" name="jobms-radio" value="-1">
                                        以错过的第一个频率时间立刻开始执行
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="jq-validation-jobparam" class="col-sm-4 control-label">Job执行参数</label>
                            <div class="col-sm-8">
                                <textarea class="form-control" id="jobparam-div" name="jq-validation-jobparam" placeholder="Job执行参数（最大1000个字符）"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">Job状态</label>
                            <div class="col-sm-8">
                                <span type="text" class="form-control"
                                      style="background: #eee;border: none;">初始状态（暂停）</span>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <!-- / .modal-body -->
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button id="addNewJob-btn" type="button" class="btn btn-primary">保 存</button>
            </div>
        </div>
    </div>
</div>



