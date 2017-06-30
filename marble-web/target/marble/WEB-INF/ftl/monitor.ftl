<!DOCTYPE html>
<!--[if IE 8]>         <html class="ie8"> <![endif]-->
<!--[if IE 9]>         <html class="ie9 gt-ie8"> <![endif]-->
<!--[if gt IE 9]><!-->
<html class="gt-ie8 gt-ie9 not-ie"> <!--<![endif]-->
<head>
<#include "common/header.ftl" encoding="UTF-8">

</head>

<body class="theme-default main-menu-animated">

<script>var init = [];</script>
<#--<script src="${base.contextPath}/resources/assets/demo/demo.js"></script>-->

<div id="main-wrapper">
<#include "common/navbar.ftl" encoding="UTF-8">
<#include "common/menu.ftl" encoding="UTF-8">

    <div id="content-wrapper">
        <!-- 导航条 -->
        <ul class="breadcrumb breadcrumb-page">
            <div class="breadcrumb-label text-light-gray">You are here:</div>
            <li><a href="#">Home</a></li>
            <li class="active"><a href="#">监控</a></li>
        </ul>

        <div class="row">
            <script>
                var uploads_data;
                init.push(function () {
                    var options = {
                        format: "yyyy-mm-dd",
                        autoclose: true,
                        todayBtn: 'linked',
                        language: 'zh-CN'
                    };
                    var datapickerRange = $('#bs-datepicker-range').datepicker(options);
                });


            </script>
            <!--
            <div class="panel">
                <div class="panel-heading">
                    <span class="panel-title"> 查询</span>
                </div>
                <div class="panel">
                    <div class="panel-body">
                        <h6 class="text-muted text-semibold text-xs" style="margin:10px 0 10px 0;">RANGE</h6>

                        <div class="input-daterange input-group" id="bs-datepicker-range"
                             style="  right: 220px;left: 20px;max-width: 800px;">
                            <input type="text" class="input-sm form-control" name="start" placeholder="开始日期">
                            <span class="input-group-addon">to</span>
                            <input type="text" class="input-sm form-control" name="end" placeholder="结束日期">
                        </div>
                        <button id="searchOrderQty-btn"
                                style="max-width: 800px;position:relative;left: 20px;margin-top: 10px;"
                                class="btn btn-labeled btn-block">
                            查询
                        </button>
                    </div>
                </div>
            </div>
            -->
            <div class="panel">
                <div class="panel-heading">
                    <span class="panel-title">监控（TODO）</span>
                    <div style="float:right;">
                        <a href="javascript:clearCache()">清空缓存</a>
                        <a href="javascript:refreshCache()">重新加载</a>
                    </div>
                </div>
                <div class="panel-body" style="padding: 0;">
                    <!-- Tab开始 -->
                    <div class="panel-body">
                        <ul id="space-detail-panel" class="nav nav-tabs" style="margin-top: 15px;">
                            <li class="active">
                                <a href="#ram-sched-panel" data-toggle="tab">缓存计划任务</a>
                            </li>
                            <li>
                                <a href="" data-toggle="tab">预留1</a>
                            </li>
                            <li>
                                <a href="" data-toggle="tab">预留2</a>
                            </li>
                        </ul>
                        <div class="tab-content tab-content-bordered">
                            <!-- 内存计划任务 -->
                            <div class="tab-pane active" id="sched-panel-manage">
                            <#include "monitor-ramscheduler.ftl" encoding="UTF-8">
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