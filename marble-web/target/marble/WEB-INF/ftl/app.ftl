<!DOCTYPE html>
<!--[if IE 8]>         <html class="ie8"> <![endif]-->
<!--[if IE 9]>         <html class="ie9 gt-ie8"> <![endif]-->
<!--[if gt IE 9]><!-->
<html class="gt-ie8 gt-ie9 not-ie"> <!--<![endif]-->
<head>
<meta charset="UTF-8">
<#include "common/header.ftl" encoding="UTF-8">
    <style type="text/css">
        #jq-datatable td{
              max-width:150px;
          }
        .btn-labeled{
            margin-right:10px;
        }
        table.panel-tb tr.child-thead{
            font-weight: bolder;
            background-color: #eee;
            color: #636060;
        }
        table.panel-tb .add-tooltip{
            width: 40px;
            height: 25px;
            font-size: 16px;
            margin-right:8px;
        }
    </style>
</head>

<body class="theme-default main-menu-animated">
<script>var init = [];</script>

<div id="main-wrapper">
<#include "common/navbar.ftl" encoding="UTF-8">
<#include "common/menu.ftl" encoding="UTF-8">

    <div id="content-wrapper">
        <!-- 导航条 -->
        <ul class="breadcrumb breadcrumb-page">
            <div class="breadcrumb-label text-light-gray">You are here:</div>
            <li><a href="#">Home</a></li>
            <li class="active"><a href="#">应用管理</a></li>
        </ul>
        <div class="row">
            <script>
                init.push(function () {
                     datatable = $('#jq-datatable').DataTable({
                        "processing": true,
                        "serverSide": true, "autoWidth": false,
                        "ajax": {
                            "url": "${basePath}/app/query",
                            "dataSrc": "result",
                            "type":"POST",
                            "data": function (data) {
                                var appCode = $("#appcode-input").val();
                                if(appCode != null && appCode.length >0){
                                    data.appCode =  appCode;
                                }
                                var appName = $("#appname-input").val();
                                if(appName != null && appName.length >0){
                                    data.appName =appName;
                                }
                                var appOwner = $("#owner-input").val();
                                if(appOwner != null && appOwner.length >0){
                                    data.appOwner =  appOwner;
                                }
                                return "_method_=get&jsonParam=" + JSON.stringify(data);
                            },
                            "error": function (data){
                                var errorMsg = (data == null || data.responseText==null)?"未知错误":data.responseText;
                                $("#jq-datatable tbody").html('<tr><td colspan="9" class="error-msg">'+errorMsg+'</td></tr>');
                            }
                        },
                        "columns": [
                            {
                                "data":
                                    function (row) {
                                        return  '<a appcode="'+row.code+'" class="row-details" style="cursor: pointer"><span class="btn-label icon fa fa-plus-square"></span>&nbsp;服务器组</a>';
                                    },"width":70,"orderable": false
                            },
                            {
                                "data":
                                        function(row) {
                                            return "<a href='${basePath}/app/"+row.code+"/scheduler' target='_blank'>"+row.code+"</a>";
                                        }
                            },
                            {"data": "name"},
                            {"data": "description"},
                            {"data": "owner","width":"70","orderable": false},
                            {"data": function(row){
                                return getAppStatusDesc(row.status);
                            }},
                            {"data": function(row) {
                                return safeString(row.marbleVersion, "N/A", 10);
                            }},
                            {"data": "soaServiceName"},
                            {"data": "soaServiceNameSpace"},
                            {"data": "createTime"},
                            {"data": "updateTime"}
                        ],
                        "columnDefs": [{
                                "targets": [11],
                                "render": function (data, type, full) {
                                     return getAppEditBtn(full.code, full.status);
                                }
                        }],
                         "initComplete": function(settings, json) {
                             $('#jq-datatable_wrapper .table-caption').text('应用列表');
                             $('#jq-datatable_wrapper .dataTables_filter input').attr('placeholder', '输入AppCode查找');
                         },
                         "order": [[1,'asc']]
                    });
                    $("#search-btn").click(function(){
                        datatable.ajax.url("${basePath}/app/query").load();
                    });

                    //详情点击
                    $("#jq-datatable tbody").on('click', 'td a.row-details', function () {
                        var nTr = $(this).parents('tr')[0];
                        var row = datatable.row(nTr);
                        if (row.child.isShown()) {
                            row.child.hide();
                            $(this).children("span.btn-label").removeClass("fa-minus-square");
                            $(this).children("span.btn-label").addClass("fa-plus-square");
                        } else {
                            row.child(getAppDetailInfo($(this).attr("appcode"))).show();
                            //添加提示信息
                            $('#jq-datatable .add-tooltip').tooltip();
                            $(this).children("span.btn-label").removeClass("fa-plus-square");
                            $(this).children("span.btn-label").addClass("fa-minus-square");
                        }
                    });
                });

                //根据app状态产生操作按钮
                function getAppEditBtn(appcode, appstatus){
                    var statusBtn = '';
                    switch(appstatus){
                        case 20://停用
                            statusBtn = '<li><a href="javascript:appOperate(this,\''+appcode+'\',\'START\');"><span class="btn-label icon fa fa-check-circle"/>&nbsp;启用</a></li><li class="divider"></li>';
                            break;
                        default://可用
                            statusBtn = '<li><a href="javascript:appOperate(this,\''+appcode+'\',\'STOP\');"><span class="btn-label icon fa fa-exclamation-circle"/>&nbsp;暂停应用</a></li><li class="divider"></li>';
                            break;
                    }
                   return '<button class="btn btn-labeled btn-info" onclick="javascript:popupEditServerDialog(\''+appcode+'\')">' +
                            '<span class="btn-label icon fa fa-edit"></span>编辑' +
                           '</button>'+
                           '<div class="btn-group">'+
                                '<button type="button" class="btn">...</button>'+
                                '<button type="button" class="btn dropdown-toggle" data-toggle="dropdown"><i class="fa fa-caret-down"></i></button>'+
                                '<ul class="dropdown-menu">'+
                                '<li><a href="javascript:popupAddServerDialog(\''+appcode+'\');"><span class="btn-label icon fa fa-plus"/>&nbsp;添加服务器</a></li>'+
                                '<li class="divider"></li>'+
                                    statusBtn+
//                                '<li><a href="javascript:popupEditServerDialog(\''+appcode+'\')"><span class="btn-label icon fa fa-edit"/>&nbsp;编辑应用</a></li>'+
                                '<li class="divider"></li>'+
                                '<li><a href="javascript:deleteApp(\''+appcode+'\')"><span class="btn-label icon fa fa-trash-o"/>&nbsp;删除应用</a></li>'+
                                '</ul>'+
                            '</div>';
                }

                //应用操作
                function appOperate(obj, appCode, opType){
                    var oper = '';
                    switch(opType){
                        case 'START': oper='start'; break;
                        case 'STOP': oper='stop'; break;
                        default: return false;
                    }
                    bootbox.confirm({
                        message: "确定要<span style='color:red;'>"+oper+"</span>应用 - "+appCode+"？",
                        callback: function(result) {
                            if(result){
                                $.ajax({
                                    "url":"${basePath}/app/"+oper,
                                    "data":{"_method_":"patch", "appCode":appCode},
                                    "type":"POST",
                                    "error":function(){
                                    },
                                    "success":function(response){
                                        if(response != null && '0000' == response.resultCode){
                                            $.growl.notice({title:"提示：",message: ""+oper+" 应用成功！"});
                                            datatable.ajax.reload( null, false);
                                        }else{
                                            $.growl.error({title:"操作失败：", message: response.resultMessage});
                                        }
                                    }
                                });
                            }
                        },
                        className: "bootbox-sm"
                    });
                }

                //编辑应用
                function popupEditServerDialog(appcode){
                    var data = new Object();
                    data.appCode = appcode;
                    $.ajax({
                        "url":"${basePath}/app/query",
                        "data":{"_method_":"get", "jsonParam":JSON.stringify(data)},
                        "type":"POST",
                        "error":function(){
                        },
                        "success":function(response){
                            if(response != null && '0000' == response.resultCode){
                                var res = response.result[0];
                                $("#editAppDialog span.edit-appcode").html(appcode);
                                $("#editAppDialog span.edit-appname").html(res.name);
                                $("#edit-appdesc-div").html(res.description);
                                $("#edit-appowner-div").val(res.owner);
                                $("#edit-appstatus-div").html(res.statusDesc);
                                $("#edit-marbleversion-div").val(res.marbleVersion);
                                $("#edit-soaservicename-div").val(res.soaServiceName);
                                $("#edit-soaservicenamespace-div").val(res.soaServiceNameSpace);
                                $("#editAppDialog").modal();
                            }else{
                                $.growl.error({title:"操作失败：", message: response.resultMessage});
                            }
                        }
                    });
                }

                //添加服务器弹窗
                function popupAddServerDialog(appcode){
                    $("#jq-validation-server-form span.app-code").html(appcode);
                    $("#addAppserverDialog").modal();
                }
                function deleteApp(appcode){
                    bootbox.dialog({
                        message:'<div style="color:red;font-size: 14px;">应用（'+appcode+'）被删除后，相关的[服务器][计划任务]等信息将会被一同移除且不可恢复！</div>',
                        title:'删除提示',
                        buttons: {
                            success: {
                                label: "取消",
                                className: "btn-cancel",
                                callback: function() {}
                            },
                            danger: {
                                label: "确定删除",
                                className: "btn-danger",
                                callback: function() {
                                    $.ajax({
                                        "url":"${basePath}/app/delete",
                                        "data":{"_method_":"delete","appCode":appcode},
                                        "type":"post",
                                        "error":function(){
                                        },
                                        "success":function(response){
                                            if(response != null && '0000' != response.resultCode){
                                                $.growl.error({title:"Delete Failed：", message: response.resultMessage});
                                            }else{
                                                $.growl.notice({title:"提示：",message: "删除应用成功！"});
                                                datatable.ajax.reload( null, false);
                                            }
                                        }
                                    });
                                }
                            }
                        },
                        className: "bootbox-sm"
                    });
                }

                //得到app状态描述
                function getAppStatusDesc(appstatus){
                    var statusDesc = '未知';
                    switch(appstatus){
                        case 10:statusDesc = '可用';break;
                        case 20:statusDesc = '停用'; break;
                    }
                    return statusDesc;
                }
                //根据appcode取得服务器详细信息
                function getAppDetailInfo(appcode){
                    var result = '';
                    if(appcode == null){
                        return result;
                    }
                    result = '<div class="col-md-6" style="width:98%;"><table class="panel-tb table table-bordered detail-table" style="clear: both">' +
                            '<tr class="child-thead"><td>服务器组</td><td>服务器名称</td><td>服务器IP</td><td>描述</td><td>操作</td></tr><tbody>';

                    $.ajax({
                        "url":"${basePath}/app/server/query",
                        "data":{"_method_":"get","appCode":appcode},
                        "type":"POST",
                        "async": false,
                        "error":function(){
                        },
                        "success":function(response){
                            if(response != null && '0000' == response.resultCode){
                                var serverList = response.result;
                                for(var index in serverList){
                                    var serverGroup = serverList[index].group;
                                    var serverName = serverList[index].name;
                                    result = result +
                                        '<tr>' +
                                            '<td class="detail-tb-title">'+serverGroup+'</td>' +
                                            '<td class="detail-tb-title">'+serverName+'</td>' +
                                            '<td class="detail-tb-title">'+serverList[index].ip+'</td>' +
                                            '<td class="detail-tb-title">'+serverList[index].description+'</td>' +
                                            '<td class="detail-tb-title">'+
                                                '<a href="#" onclick="javascript:serverOperate(this,\''+appcode+'\',\''+serverGroup+'\',\''+serverName+'\',\''+serverList[index].ip+'\',\'EDIT\');return false;" title="" class="btn btn-xs btn-outline btn-success add-tooltip" data-original-title="Edit"><i class="fa fa-pencil"></i></a>'+
                                                '<a href="#" onclick="javascript:serverOperate(this,\''+appcode+'\',\''+serverGroup+'\',\''+serverName+'\',\''+serverList[index].ip+'\',\'DELETE\');return false;" title="" class="btn btn-xs btn-outline btn-danger add-tooltip" data-original-title="Delete"><i class="fa fa-times"></i></a>'+
                                            '</td>' +
                                        '</tr>';
                                }
                            }else{
                                $.growl.error({title:"Delete Failed：", message: response.resultMessage});
                            }
                        }
                    });
                    return result + '</tbody></table></div>';
                }

                //server操作
                function serverOperate(obj, appCode, serverGroup, serverName, serverIp, operType){
                    switch(operType){
                        case 'EDIT':
                            bootbox.alert("<strong>Oh sorry!</strong> 开发中... 敬请期待！"); return;
                        case 'DELETE': deleteServer(appCode, serverGroup, serverName,serverIp);break;
                        default: return false;
                    }
                }

                //删除server
                function deleteServer(appCode, serverGroup, serverName, serverIp){
                    bootbox.confirm({
                        message: "确定要删除服务器【"+serverGroup+" - "+serverName+"】？",
                        callback: function(result) {
                            if(result){
                                $.ajax({
                                    "url":"${basePath}/app/server/delete",
                                    "data":{"_method_":"delete","appCode":appCode, "serverGroup":serverGroup, "serverName":serverName,"serverIp":serverIp},
                                    "type":"post",
                                    "error":function(){
                                    },
                                    "success":function(response){
                                        if(response != null && '0000' != response.resultCode){
                                            $.growl.error({title:"Delete Failed：", message: response.resultMessage});
                                        }else{
                                            $.growl.notice({title:"提示：",message: "删除服务器成功！"});
                                            datatable.ajax.reload( null, false);
                                        }
                                    }
                                });
                            }
                        },
                        className: "bootbox-sm"
                    });
                }

            </script>

            <div class="panel">
                <div class="panel-heading">
                    <span class="panel-title" style="font-weight: bolder;">应用管理</span>
                </div>
                <table class="table search-table" id="inputs-table" style="margin: 10px 0px 0px 15px;">
                    <tbody>
                    <tr>
                        <td style="width: 100px;vertical-align:middle;">应用Code：</td>
                        <td style="width: 200px;vertical-align:middle;"><input id="appcode-input" type="text" class="form-control" placeholder="输入APPIP"></td>
                        <td style="width: 100px;vertical-align:middle;">应用名：</td>
                        <td style="width: 200px;vertical-align:middle;"><input id="appname-input" type="text" class="form-control" placeholder="输入应用名称"></td>
                        <#if Account.hasAdminRole>
                        <td style="width: 100px;vertical-align:middle;">拥有者：</td>
                        <td style="width: 200px;vertical-align:middle;"><input id="owner-input" type="text" class="form-control" placeholder="输入拥有者员工号"></td>
                        </#if>
                        <td style="vertical-align:middle;">
                            <button type="button" id="search-btn" class="btn btn-primary" style="width: 70px;">查询</button>
                        </td>
                        <#if Account.hasAdminRole>
                            <td>
                                <div class="pull-right col-xs-12 col-sm-auto" style="margin-right:40px;">
                                    <button class="btn btn-primary btn-labeled" data-toggle="modal" data-target="#addNewAppDialog">
                                        <span class="btn-label icon fa fa-plus"></span>添加
                                    </button>
                                </div>
                            </td>
                        </#if>
                    </tr>
                    </tbody>
                </table>
                <#include "app-addpopup.ftl" encoding="UTF-8">
            <#include "app-editpopup.ftl" encoding="UTF-8">
                <#include "app-server-addpopup.ftl" encoding="UTF-8">
                <div class="panel-body">
                    <div class="table-primary">
                        <table id="jq-datatable" cellpadding="0" cellspacing="0" border="0"
                               class="table table-striped table-bordered dataTable no-footer"
                               aria-describedby="jq-datatables-example_info">
                            <thead>
                            <tr>
                                <th></th>
                                <th>应用Code</th>
                                <th>应用名称</th>
                                <th>应用描述</th>
                                <th>所有者（员工号）</th>
                                <th>应用状态</th>
                                <th>Marble版本号</th>
                                <th>SOA服务名</th>
                                <th>SOA服务NameSpace</th>
                                <th>创建时间</th>
                                <th>最后更新时间</th>
                                <th style="width: 200px;">操作</th>
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