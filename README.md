# Marble 简介
Marble是一款Java实现的计划任务调度框架。目的是:
1)提供基本的java版JOB调度功能; 
2)最大限度的使开发java job时只关注实现业务逻辑,透明化Job底层实现; 
3)提供对job调度的统一管理和配置;

# Marble 结构
Marble分服务端和OFFLINE端两部分。服务端与Spring整合，提供spring标签配置job信息；OFFLINE提供web页面进行job的发现及配置（以后可能会引入zookeeper等自动发现）。

# 实现简介
Marble的核心其实有两个部分: 1)Job的调度; 2)与服务端的通讯;
Marble的job调度使用了Quartz – 一款流行的开源作业调度框架；
与Job服务端的通讯使用了Apache Thrift - 一款开源的高效RPC框架。（2.0版支持Netty） ；
Marble在两个开源框架基础上做了必要的封装和针对公司使用的定制化修改。

# 关于作者
Java菜鸟。联系方式djx_19881022@163.com
