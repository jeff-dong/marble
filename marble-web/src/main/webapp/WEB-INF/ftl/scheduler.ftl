<!DOCTYPE html>
<!--[if IE 8]>         <html class="ie8"> <![endif]-->
<!--[if IE 9]>         <html class="ie9 gt-ie8"> <![endif]-->
<!--[if gt IE 9]><!-->
<html class="gt-ie8 gt-ie9 not-ie"> <!--<![endif]-->
<head>
<#include "common/header.ftl" encoding="UTF-8">
    <style>
        .btn {
            margin: 5px auto 5px auto;
        }
    </style>
    <script>
        var init = [], joblogDT,datatableSchedManage;
    </script>
</head>

<body class="theme-default main-menu-animated">


<div id="main-wrapper">
<#include "common/navbar.ftl" encoding="UTF-8">
<#include "common/menu.ftl" encoding="UTF-8">

    <div id="content-wrapper">
        <!-- 导航条 -->
        <ul class="breadcrumb breadcrumb-page">
            <div class="breadcrumb-label text-light-gray">You are here:</div>
            <li><a href="#">Home</a></li>
            <li><a href="${basePath}/app">应用</a></li>
            <li class="active"><a href="${basePath}/app/${APP_CODE}/scheduler">计划任务列表</a></li>
        </ul>
        <div class="row">
            <!-- -->
            <div class="panel">
                <div class="panel-heading">
                    <span class="panel-title" style="font-weight: bolder;">计划任务</span>
                </div>
                <div class="panel-body">
                    <ul id="space-detail-panel" class="nav nav-tabs" style="margin-top: 15px;">
                        <li class="active">
                            <a href="#sched-panel-manage" data-toggle="tab">计划任务管理</a>
                        </li>
                        <li>
                            <a href="#sched-panel-joblog" data-toggle="tab">执行日志查询</a>
                        </li>
                        <li>
                            <a href="#space-panel-trigger" data-toggle="tab">监控</a>
                        </li>
                    </ul>
                    <div class="tab-content tab-content-bordered">
                        <!-- 计划任务管理Tab-->
                        <div class="tab-pane active" id="sched-panel-manage" appcode="${APP_CODE}">
                            <#include "scheduler-manage.ftl" encoding="UTF-8">
                        </div>
                        <!-- 日志查询Tab -->
                        <div class="tab-pane" id="sched-panel-joblog" appcode="${APP_CODE}">
                            <#include "scheduler-log.ftl" encoding="UTF-8">
                        </div>
                        <!-- trigger -->
                        <div class="tab-pane" id="space-panel-trigger" appcode="${APP_CODE}">
                            <div class="note note-info">
                                <div style="font-size: 29px;color: red;font-weight: bolder;text-align: center;">
                                    建设中 &gt;&gt; 敬请期待...
                                </div>
                            </div>
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
<script src="${basePath}/resources/js/cronGen.js"></script>
</body>
</html>