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
            <li><a href="#">数据管理（未完成）</a></li>
            <li class="active"><a href="${basePath}/dataManage/job">jobs</a></li>
        </ul>
        <div class="row">

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