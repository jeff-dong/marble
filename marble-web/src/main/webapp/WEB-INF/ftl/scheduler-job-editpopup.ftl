<script>
    init.push(function () {
        $('#job-edit-jobcron').tooltip();

        // Setup validation
        $("#vad-job-edit-form").validate({
            ignore: '.ignore, .select2-input',
            focusInvalid: false,
            rules: {
                'vad-job-edit-jobdesc': {
                    required: true,
                    maxlength: 500
                },
                'vad-job-edit-jobcron': {
                    required: true,
                    maxlength: 50
                },
                'vad-job-edit-jobparam': {
                    required: true,
                    maxlength: 500
                },
                'job-edit-jobms': {
                    required: true
                }
            }
        });

        $("#updateJob-btn").click(function () {
            var validateResult = $("#vad-job-edit-form").valid();
            if (!validateResult) {
                return;
            }

            var schedName = $("#vad-job-edit-form span.job-edit-schedname").text();
            var jobName = $("#vad-job-edit-form span.job-edit-jobname").text();
            var jobCron = $("#job-edit-jobcron").val();
            var jobDesc = $("#job-edit-jobdesc").val();
            var jobParam = $("#job-edit-jobparam").val();
            var jobMisFire = $('input[name="job-edit-jobms"]:checked').val();
            $.ajax({
                "url": "${basePath}/app/scheduler/job/update",
                "data": {
                    "_method_": "patch",
                    "app.code":${appCode},
                    "scheduler.name": schedName,
                    "name": jobName,
                    "cronExpress": jobCron,
                    "param": jobParam,
                    "description": jobDesc,
                    "misfireStrategy":jobMisFire
                },
                "type": "POST",
                "error": function () {
                },
                "success": function (response) {
                    if (response == null || '0000' != response.resultCode) {
                        $.growl.error({title: "更新失败：", message: response.resultMessage});
                    } else {
                        $.growl.notice({title: "提示：", message: "更新成功！"});
                        $('#editJobDialog').modal("hide");
                        location.reload();
                    }
                }
            });
        });
    });

    function populateJobInfoForEditDialog(appCode, schedName, jobName) {
        if (appCode == null || schedName == null || jobName == null) {
            return false;
        }
        $.ajax({
            "url": "${basePath}/app/scheduler/job/query",
            "data": {"_method_": "get", "appCode": appCode, "schedName": schedName, "jobName": jobName},
            "type": "POST",
            "async": false,
            "error": function () {
            },
            "success": function (response) {
                if (response != null && '0000' == response.resultCode) {
                    var jobList = response.result;
                    if (jobList != null && jobList.length == 1) {
                        var jobInfp = jobList[0];
                        $("#vad-job-edit-form span.job-edit-schedname").html(schedName);
                        $("#vad-job-edit-form span.job-edit-jobname").html(jobName);
                        $("#job-edit-jobdesc").html(jobInfp.description);
                        $("#job-edit-jobcron").val(jobInfp.cronExpress);
                        $("#job-edit-jobparam").val(jobInfp.param);
                        $("input[type='radio'][name='job-edit-jobms'][value='"+jobInfp.misfireStrategy+"']").attr("checked",true);
                        $("#vad-job-edit-form span.job-edit-jobstatus").html(jobInfp.statusDesc);
                    }
                }
            }
        });

    }
</script>

<!-- 更新Job -->
<div id="editJobDialog" class="modal fade" tabindex="-1" role="dialog" style="display: none;" aria-hidden="true">
    <div class="modal-dialog" style="width:800px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">更新Job</h4>
            </div>
            <div class="modal-body">
                <div class="alert">
                    <button type="button" class="close" data-dismiss="alert">×</button>
                    <i class="panel-title-icon fa fa-bullhorn"></i>
                    <strong>提示：</strong>如果更新Job的Cron表达式或者执行参数，运行中的Job会被<span style="color:red;">暂停</span>，需要手动开启
                </div>
                <form class="form-horizontal" id="vad-job-edit-form" novalidate="novalidate">
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="col-sm-4 control-label">应用Code</label>

                            <div class="col-sm-8">
                                <span class="form-control job-edit-appcode"
                                      style="background: #eee;border: none;">${appCode}</span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">计划任务名称</label>

                            <div class="col-sm-8">
                                <span class="form-control job-edit-schedname"
                                      style="background: #eee;border: none;"></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="vad-job-edit-jobname" class="col-sm-4 control-label">Job名称</label>

                            <div class="col-sm-8">
                                <span class="form-control job-edit-jobname"
                                      style="background: #eee;border: none;"></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="vad-job-edit-jobdesc" class="col-sm-4 control-label">Job描述</label>

                            <div class="col-sm-8">
                                <textarea class="form-control" id="job-edit-jobdesc" name="vad-job-edit-jobdesc"
                                          placeholder="描述下这是个什么鬼..."></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="vad-job-edit-jobcron" class="col-sm-4 control-label">Cron表达式</label>

                            <div class="col-sm-8">
                                <input type="text" class="form-control tooltip-warning" id="job-edit-jobcron"
                                       name="vad-job-edit-jobcron" placeholder="Cron表达式" data-toggle="tooltip" data-placement="bottom" data-original-title="提示：请保证执行频率>=1分钟">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="vad-job-edit-jobdesc" class="col-sm-4 control-label">MisFire策略</label>

                            <div class="col-sm-8">
                                <div class="radio" style="margin-top: 0;">
                                    <label>
                                        <input type="radio" name="job-edit-jobms" value="2">
                                        不触发立即执行(等待下次Cron触发频率到达时刻开始按照Cron频率依次执行)
                                    </label>
                                </div>
                                <div class="radio" style="margin-top: 0;">
                                    <label>
                                        <input type="radio" name="job-edit-jobms" value="1">
                                        以当前时间为触发频率立刻触发一次执行
                                    </label>
                                </div>
                                <div class="radio" style="margin-bottom: 0;">
                                    <label>
                                        <input type="radio" name="job-edit-jobms" value="-1">
                                        以错过的第一个频率时间立刻开始执行
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="vad-job-edit-jobparam" class="col-sm-4 control-label">Job执行参数</label>

                            <div class="col-sm-8">
                                <input type="text" class="form-control" id="job-edit-jobparam"
                                       name="vad-job-edit-jobparam" placeholder="Job执行参数">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label">Job状态</label>

                            <div class="col-sm-8">
                                <span type="text" class="form-control job-edit-jobstatus"
                                      style="background: #eee;border: none;"></span>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <!-- / .modal-body -->
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button id="updateJob-btn" type="button" class="btn btn-primary">保 存</button>
            </div>
        </div>
    </div>
</div>


