<!DOCTYPE html>
<!--[if IE 8]>         <html class="ie8"> <![endif]-->
<!--[if IE 9]>         <html class="ie9 gt-ie8"> <![endif]-->
<!--[if gt IE 9]><!-->
<html class="gt-ie8 gt-ie9 not-ie"> <!--<![endif]-->
<head>
<meta charset="UTF-8">
<#include "common/header.ftl" encoding="UTF-8">
    <style type="text/css">

    </style>
</head>

<body class="theme-default main-menu-animated">
<script>var init = [];</script>

<div id="main-wrapper">
<#include "common/navbar.ftl" encoding="UTF-8">
<#include "common/menu.ftl" encoding="UTF-8">

    <div id="content-wrapper">
        <div class="row">
            <h2 class="col-sm-8 text-center text-left-sm text-slim">
                常见问题
            </h2>
            <form action="" class="col-sm-4 form-faq">
                <input type="text" placeholder="Enter keyword or question..." class="form-control input-sm rounded">
            </form>
        </div>


        <div class="row" style="margin-top: 20px;">
            <div class="col-sm-4 col-sm-push-8">
                <div class="panel no-border panel-dark">
                    <div class="panel-body text-center">
                        <p class="text-lg text-slim">
                            Can't find the answer?
                        </p>
                        <div style="margin-top: 20px;">
                            <a href="#" target="_blank" class="btn btn-lg btn-primary btn-flat" >Confluence文档</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-8 col-sm-pull-4">
                <div class="panel-group" id="accordion-example">
                    <div class="panel">
                        <div class="panel-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-example" href="#collapseOne">
                                <strong>怎样联系到管理员？</strong>
                            </a>
                        </div> <!-- / .panel-heading -->
                        <div id="collapseOne" class="panel-collapse in">
                            <div class="panel-body">
                                <strong>发邮件到：</strong>dongjianxing@aliyun.com
                            </div> <!-- / .panel-body -->
                        </div> <!-- / .collapse -->
                    </div> <!-- / .panel -->

                    <div class="panel">
                        <div class="panel-heading">
                            <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion-example" href="#collapseTwo">
                                <strong>怎样添加一个应用？</strong>
                            </a>
                        </div> <!-- / .panel-heading -->
                        <div id="collapseTwo" class="panel-collapse collapse">
                            <div class="panel-body">
                                <strong>TODO</strong>
                            </div> <!-- / .panel-body -->
                        </div> <!-- / .collapse -->
                    </div> <!-- / .panel -->
                </div> <!-- / .panel-group -->
            </div>
        </div>

    </div>
    <!-- / #content-wrapper -->
    <div id="main-menu-bg"></div>
</div>
<!-- / #main-wrapper -->
<#include "common/js.ftl" encoding="UTF-8">

</body>
</html>