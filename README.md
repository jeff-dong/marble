
#Marble简介

Marble是一款Java实现的JOB调度框架。目的是：
1）填补目前xxx公司没有java版调度框架的缺憾；
2）最大限度的使开发java job时只关注实现业务逻辑，透明化Job底层实现；
3）提供对job调度的统一管理和配置；

Marble分服务端和OFFLINE端两部分。
服务端>>
	服务端以jar包形式存在。目前版本(2.0.0)仅支持与Spring的整合使用。服务端提供Spring的自定义标签可最大限度简化开发人员接入Marble。
详情见<<服务端使用>>章节

OFFLINE>>
	为了可视化管理JOB，提供一个前端基于Bootstrap, 后端采用SpringMVC + MyBatis + MySql结构的控制台。提供基本的JOB的增删改查，启动/暂停等操作。

Marble部署图如下：
![](https://github.com/jeff-dong/marble/blob/master/document/images/marble_deployment__diagram.png) 

目前版本Marble提供的功能如下：
* 基本JOB管理（增删改查、暂停/启动）；
* JOB执行参数传递的支持；
* CRON表达式生成控件；
* 多种Misfire策略配置；
* 集群环境部署（不重复执行）；
* 集群的软负载均衡；
* Spring整合；

#Marble使用

##接入Marble步骤：

服务端=>
1. 引入相关Jar包(Netty等)<br /> 
2. 定义Job的具体实现类（Spring中的Bean）<br /> 
3. Spring中配置Job的实现类<br /> 

OFFLINE端=>
1. 配置对应应用和Job信息（配合权限管理）<br /> 

####接入Marble - 服务端
1、引入相关Jar包（以Maven为例）
注：依赖marble-agent包（自己通过源代码的marble-server模块生成）和Thrift包
···XML
<!-- Marble相关 -->
<dependency>
    <groupId>com.github.jxdong</groupId>
    <artifactId>marble-agent</artifactId>
    <version>1.0.1</version>
</dependency>
 
<!-- thrift相关（2.0.0之前版本需要） -->
<dependency>
	<groupId>org.apache.thrift</groupId>
	<artifactId>libthrift</artifactId>
	<version>0.9.3</version>
</dependency>

<!-- Netty相关 -->
<dependency>
	<groupId>io.netty</groupId>
	<artifactId>netty-all</artifactId>
	<version>5.0.0.Alpha2</version>
</dependency>
####接入Marble - OFFLINE端


# 关于作者
Java菜鸟。联系方式djx_19881022@163.com
