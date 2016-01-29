<!DOCTYPE html>
<!--[if IE 8]>         <html class="ie8"> <![endif]-->
<!--[if IE 9]>         <html class="ie9 gt-ie8"> <![endif]-->
<!--[if gt IE 9]><!-->
<html class="gt-ie8 gt-ie9 not-ie"> <!--<![endif]-->
<head>
<meta charset="UTF-8">
<#include "common/header.ftl" encoding="UTF-8">

</head>

<body class="theme-default main-menu-animated">
<style>
    #jq-datatable td{
        max-width:300px;
    }
</style>
<script>var init = [];</script>

<div id="main-wrapper">
<#include "common/navbar.ftl" encoding="UTF-8">
<#include "common/menu.ftl" encoding="UTF-8">

    <div id="content-wrapper">
        <!-- 导航条 -->
        <ul class="breadcrumb breadcrumb-page">
            <div class="breadcrumb-label text-light-gray">You are here:</div>
            <li><a href="#">Home</a></li>
            <li class="active"><a href="#">Key-Value配置</a></li>
        </ul>
        <div class="row">
            <script>
                init.push(function () {
                     datatable = $('#jq-datatable').DataTable({
                        "processing": true,
                        "serverSide": true,
                         "autoWidth": false,
                        "ajax": {
                            "url": "${basePath}/configure/query",
                            "dataSrc": "result",
                            "data": function (data) {
                                var keyArray= new Array();
                                var keyStr = $("#key-input").val();
                                if(keyStr!=null && keyStr.length>0){
                                    keyArray=keyStr.split(",");
                                    data.key =  keyArray;
                                }
                                var groupArray= new Array();
                                var groupStr = $("#group-input").val();
                                if(groupStr!=null && groupStr.length>0){
                                    groupArray=groupStr.split(",");
                                    data.group =  groupArray;
                                }
                                return "jsonParam=" + JSON.stringify(data);
                            },
                            "error": function (data){
                                var errorMsg = (data == null || data.responseText==null)?"未知错误":data.responseText;
                                $("#jq-datatable tbody").html('<tr><td colspan="8" class="error-msg">'+errorMsg+'</td></tr>');
                            }

                        },
                        "columns": [
                            {"data": "id"},
                            {"data": "group"},
                            {"data": "key"},
                            {
                                "data": function (row) {
                                    if(row.group =='TASK_DISTRIBUTION'){
                                        var result = "<span conid='"+row.id+"'>";
                                        var valueArray = row.value.split(";");
                                        for(var tem in valueArray){
                                            result = result + valueArray[tem] + ";<br>";
                                        }
                                        return result + "</span>";
                                    }else{
                                        return "<span conid='"+row.id+"'>"+row.value+"</span>";
                                    }
                                }
                            },
                            {
                                "data": function (row) {
                                    return "<span conid='"+row.id+"'>"+row.description+"</span>";
                                }
                            },
                            {"data": "createTime","dateFormat": "yy-mm-dd"},
                            {"data": "updateTime"}
                        ],
                        "columnDefs": [{
                                "targets": [7],
                                "render": function (data, type, full) {
                                    var resultVal = ' <button class="btn btn-outline btn-labeled btn-danger" onclick="deleteConfigItem(\''+full.id+'\',\''+full.key+'\')"><span class="btn-label icon fa  fa-trash-o"></span>删除</button>';
                                        return "<button class='btn btn-info edit-btn'>编辑</button>" + resultVal;
                                }
                        }],
                         "initComplete": function(settings, json) {
                             $('#jq-datatable_wrapper .table-caption').text('配置列表');
                             $('#jq-datatable_wrapper .dataTables_filter input').attr('placeholder', '输入ID查找');
                         },
                    });
                    $("#search-btn").click(function(){
                        datatable.ajax.url("${basePath}/configure/query").load();
                    });

                    //编辑事件
                    $("#jq-datatable tbody").on("click",".edit-btn",function(){
                        var tds=$(this).parents("tr").children();
                        $.each(tds, function(i,val){
                            var jqob=$(val);
                            if(i == 3){
                                jqob.html('<input conid="'+jqob.find("span").attr("conid")+'" type="text" class="form-control" value="'+jqob.find("span").text()+'">');
                            }else if(i ==4){
                                jqob.html('<textarea conid="'+jqob.find("span").attr("conid")+'" type="text" class="form-control" style="width: 300px;" value="'+jqob.find("span").text()+'">'+jqob.find("span").text()+'</textarea>');
                            }
                        });
                        $(this).html("保存");
                        $(this).toggleClass("edit-btn");
                        $(this).toggleClass("btn-success");
                        $(this).toggleClass("btn-info");
                        $(this).toggleClass("save-btn");
                    });

                    //保存事件
                    $("#jq-datatable tbody").on("click",".save-btn",function(){
                        var btnObj = $(this);
                        var tds=$(this).parents("tr").children();

                        var newValue = null,conId=null, newDesc=null;
                        var jqob3, jqob4;
                        $.each(tds, function(i,val){
                            if(i==3){
                                jqob3=$(val);
                                conId = jqob3.children("input").attr("conid");
                                newValue = jqob3.children("input").val();
                            }else if(i==4){
                                jqob4 = $(val);
                                newDesc = jqob4.children("textarea").val();;
                            }
                        });
                        if(conId!=null &&newValue!=null){
                            var updateUrl = "${basePath}/configure/update";
                            $.ajax({
                            "url":updateUrl,
                                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                                "data":{"_method_":"patch","id":conId,"value":newValue, "description":newDesc},
                                "type":"POST",
                                "error":function(){
                                },
                                "success":function(response){
                                    if(response != null && '0000' != response.resultCode){
                                        $.growl.error({title:"Update Failed：", message: response.resultMessage});
                                    }else{
                                        jqob3.html('<span conid="'+conId+'" value="'+newValue+'">'+newValue+'</span>');
                                        jqob4.html('<span conid="'+conId+'" value="'+newDesc+'">'+newDesc+'</span>');
                                        btnObj.html("编辑");
                                        btnObj.toggleClass("edit-btn");
                                        btnObj.toggleClass("btn-success");
                                        btnObj.toggleClass("btn-info");
                                        btnObj.toggleClass("save-btn");
                                    }
                                }
                            });
                        }
                    });
                });
                function deleteConfigItem(recId, key){
                    if(recId == null || key == null){
                        return;
                    }
                    bootbox.confirm({
                        message: "确定要删除【"+key+"】？",
                        callback: function(result) {
                            if(result){
                                $.ajax({
                                    "url":"${basePath}/configure/delete",
                                    "data":{"_method_":"delete","primaryKey":recId},
                                    "type":"POST",
                                    "error":function(){
                                    },
                                    "success":function(response){
                                        if(response != null && '0000' != response.resultCode){
                                            $.growl.error({title:"Update Failed：", message: response.resultMessage});
                                        }else{
                                            location.reload();
                                        }
                                    }
                                });
                            }
                        },
                        className: "bootbox-sm"
                    });
                }
                //刷新权限缓存
                function refreshAuthority(){

                }
            </script>

            <div class="panel">
                <div class="panel-heading">
                    <span class="panel-title" style="font-weight: bolder;">Key-Value 配置</span>
                </div>
                <#include "configure-addpopup.ftl" encoding="UTF-8">
                <table class="table search-table" id="inputs-table" style="margin: 10px 0px 0px 15px;">
                    <tbody>
                    <tr>
                        <td style="width: 60px;vertical-align:middle;">组名：</td>
                        <td style="width: 400px;vertical-align:middle;"><input id="group-input" type="text" class="form-control" placeholder="如输入多个，请用,分隔"></td>
                        <td style="width: 30px;vertical-align:middle;">键：</td>
                        <td style="width: 400px;vertical-align:middle;"><input id="key-input" type="text" class="form-control" placeholder="如输入多个，请用,分隔"></td>
                        <td style="vertical-align:middle;">
                            <button type="button" id="search-btn" class="btn btn-primary" style="width: 70px;">查询</button>
                        </td>
                        <td>
                            <div class="pull-right col-xs-12 col-sm-auto" style="margin-right:40px;">
                                <button class="btn btn-primary btn-labeled" data-toggle="modal" data-target="#addConfigureDialog">
                                    <span class="btn-label icon fa fa-plus"></span>添加
                                </button>
                            </div>
                        </td>
                        <td>
                            <div class="pull-right col-xs-12 col-sm-auto" style="margin-right:40px;">
                                <button class="btn" onclick="javascript:refreshAuthority();">
                                    <span class="btn-label icon fa fa-refresh"></span>刷新缓存
                                </button>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="panel-body">
                    <div class="table-primary">
                        <table id="jq-datatable" cellpadding="0" cellspacing="0" border="0"
                               class="table table-striped table-bordered dataTable no-footer"
                               aria-describedby="jq-datatables-example_info">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>组别</th>
                                <th>键</th>
                                <th>键值</th>
                                <th>描述</th>
                                <th>创建时间</th>
                                <th>最后更新时间</th>
                                <th style="width: 140px;">操作</th>
                            </tr>
                            </thead>
                        </table>
                        <div class="table-footer clearfix">

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Page wide horizontal line -->
        <hr class="no-grid-gutter-h grid-gutter-margin-b no-margin-t">

    </div>
    <!-- / #content-wrapper -->
    <div id="main-menu-bg"></div>
</div>
<!-- / #main-wrapper -->
<#include "common/js.ftl" encoding="UTF-8">

</body>
</html>