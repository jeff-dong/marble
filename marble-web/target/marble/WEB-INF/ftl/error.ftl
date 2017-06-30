
<!DOCTYPE html>
<!--[if IE 8]>         <html class="ie8"> <![endif]-->
<!--[if IE 9]>         <html class="ie9 gt-ie8"> <![endif]-->
<!--[if gt IE 9]><!--> <html class="gt-ie8 gt-ie9 not-ie"> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Error - Pages</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">

   <#include "common/header.ftl" encoding="UTF-8">
    <link href="${base.contextPath}/resources/assets/stylesheets/pages.min.css" rel="stylesheet" type="text/css">
</head>
<body class="page-404">
<script>var init = [];</script>

<div class="header">
    <a href="index.html" class="logo">
        <strong>MARBLE OFFLINE</strong>
    </a> <!-- / .logo -->
</div> <!-- / .header -->

<div class="error-text">
    <br>
</div>
<div class="error-code">OOPS!</div>
<div class="error-code" style="font-size: 60px;color:#F44F4F;"><span>${errorCode}</span>: ${errorMsg}</div>

<#include "common/js.ftl" encoding="UTF-8">

</body>
</html>