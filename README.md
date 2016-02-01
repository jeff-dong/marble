
#Marble简介

Marble是一款Java实现的JOB调度框架。目的是：
1）填补目前xxx公司没有java版调度框架的缺憾；
2）最大限度的使开发java job时只关注实现业务逻辑，透明化Job底层实现；
3）提供对job调度的统一管理和配置；

Marble分服务端和OFFLINE端两部分。

##服务端
服务端以jar包形式存在。目前版本(2.0.0)仅支持与Spring的整合使用。服务端提供Spring的自定义标签可最大限度简化开发人员接入Marble。
详情见<<服务端使用>>章节

##OFFLINE
为了可视化管理JOB，提供一个前端基于Bootstrap, 后端采用SpringMVC + MyBatis + MySql结构的控制台。提供基本的JOB的增删改查，启动/暂停等操作。


# 关于作者
Java菜鸟。联系方式djx_19881022@163.com
