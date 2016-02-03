##<a name="index"/>目录
* [Marble简介](#introduction)
* [Marble使用](#usage)
    * [接入Marble步骤](#usage_access)
        - * [接入Marble - 服务端](#usage_access_server_side)
        - * [接入Marble - OFFLINE端](#usage_access_offline_side)
* [Marble原理介绍](#principle) 
    * [Marble实现原理 - 一次JOB调度过程](#principle_job_dispatch) 
    * [Marble实现原理 - 集群支持](#principle_cluster_impl)
    * [Marble实现原理 - Misfire策略](#principle_misfire_impl)
* [后续计划](#development_plan)
* [关于作者](#author_about)
   

#<a name="introduction"/> Marble简介

Marble是一款Java实现的JOB调度框架。目的是：<br/>
* 1）填补目前xxx公司没有java版调度框架的缺憾；
* 2）最大限度的使开发java job时只关注实现业务逻辑，透明化Job底层实现；
* 3）提供对job调度的统一管理和配置；

Marble分服务端和OFFLINE端两部分。<br/>
服务端>>
    服务端以jar包形式存在。目前版本(2.0.0)仅支持与Spring的整合使用。服务端提供Spring的自定义标签可最大限度简化开发人员接入Marble。
详情见<< [Marble使用](#usage)>>章节

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

<a name="usage"/>
#Marble使用

<a name="usage_access"/>
##接入Marble步骤

服务端=>
1. 引入相关Jar包(Netty等)
1. 定义Job的具体实现类（Spring中的Bean）
1. Spring中配置Job的实现类

OFFLINE端=>
1. 配置对应应用和Job信息（配合权限管理）

<a name="usage_access_server_side"/>
####接入Marble - 服务端
1. 引入相关Jar包（以Maven为例）<br/>
注：依赖marble-agent包（自己通过源代码的marble-server模块生成）和Thrift包
```java  
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
```

2.定义Job具体实现类<br/>
定义java类继承父类MarbleJob，覆盖方法execute (当Job被调用时会执行execute下的内容)。
```java  
@Component
public class MarbleJob1 extends MarbleJob {
 
    private static final Logger logger = LoggerFactory.getLogger(MarbleJob1.class);
 
    @Override
    public void execute(String param) {
        logger.debug("******** MarbleJob1 开始执行啦 ********");
    }
}
```

3. Spring中添加Job配置<br/>

marble框架中已经自定义了Spring标签<marble ..>方便直接配置。
示例配置如下：
```java 
<marble:scheduler id="Scheduler001" port="9091" appCode="8890">
    <marble:job name="Job1" description="jobDescription1" ref="marbleJob1"/>
    <marble:job name="Job2" description="jobDescription2" ref="marbleJob2"/>
</marble:scheduler>
```

配置解释：<br/>

配置了一个名称为Scheduler001的计划任务（job组），在9091端口上暴露服务。<br/>
Scheduler001下包含了两个Job：job1和job2.<br/>
Job1的实现类是 Spring bean  - marbleJob1；<br/>
Job2的实现类是Spring Bean – marbleJob2<br/>

Marble标签详解：<br/>
\<marble:scheduler/\>: 计划任务配置。一个应用可以配置多个计划任务，每个计划任务暴露在一个IP的端口下，一个计划任务中可以包含多个Job。

| 属性        |    是否必填      | 描述   |
| ------------- |:-------------:| -----:|
| id | 必填 | ID属性，英文+数字的组合来定义该计划任务的唯一标识。|
| host | 非必填      |   所在服务器的IP地址。指定后Marble会尝试将服务暴露在该IP下。不是必填项。由于集群，建议该字段不填，Marble默认会自动获取； |
| port| 必填| 计划任务暴露的端口号。指明该计划任务暴露在机器的哪个端口下，Marble随Spring启动后会尝试打开本机的该端口并暴露服务。|
| appCode |（必填） | 所在应用的APPID。为了在同一台机器上区别不同的应用，以免调用时混淆。|

\<marble:job/\>: 计划任务下的job的配置。指明具体任务的别名以及实现类。

| 属性        |    是否必填      | 描述   |
| ------------- |:-------------:| -----:|
| name | 必填 | 给当前Job起的别名，在所属的scheduler下必须唯一。|
| description | 非必填 | ob的描述信息，仅用来便于开发者自己识别，非必填。|
| ref | 必填 | Job的具体实现类，必须指向存在的一个Spring Bean。值为bean的name。且该bean必须继承自MarbleJob并覆盖execute方法。Job被调用时会执行execute方法，因此执行逻辑要写在execute中。|


<a name="usage_access_offline_side"/>
####接入Marble - OFFLINE端

1、添加相关应用<br/>

![](https://github.com/jeff-dong/marble/blob/master/document/images/marble_offline_addapp.png) 
* 应用Code：唯一的APPID
* 应用name：应用name必须与server端中<marble:scheduler/>标签的appCode属性值一致
* 应用描述
* 应用拥有者：输入员工号
* Marble版本号：2.0.0开始支持Netty

2、应用下添加运行的服务器（可添加多台）<br/>

![](https://github.com/jeff-dong/marble/blob/master/document/images/marble_offline_addserver.png) 
* 服务器组：默认DEFAULT
* 服务器name：组内唯一
* 服务器描述

3、应用下添加Scheduler <br/>

![](https://github.com/jeff-dong/marble/blob/master/document/images/marble_offline_addSched.png) 
* Scheduler名称：唯一Scheduler
* Scheduler 描述
* 服务器相关信息（点击选中输入打开的端口号）

4、Scheduler下添加多个Job <br/>

![](https://github.com/jeff-dong/marble/blob/master/document/images/marble_offline_addjob.png) 
* Job名称：唯一job
* job 描述
* Cron表达式： 通过控件选择（具体含义，请 自行百度Cron表达式）
* Misfire策略选择（目前提供三种方式）
* Job执行参数：String类型

<a name="principle"/>
#Marble原理介绍

Marble的核心其实有两个部分：<br/>
* 1）Job的调度；
* 2）与服务端的通讯；

Marble的job调度使用了Quartz – 一款流行的开源作业调度框架。<br/>

与Job服务端的通讯使用了Apache Thrift / Netty(2.0.0开始) - 一款开源的高效RPC框架。<br/>

Marble在两个开源框架基础上做了必要的封装和针对公司使用的定制化修改。

<a name="principle_job_dispatch"/>
### Marble实现原理 - 一次JOB调度过程

调度服务与job服务交互：
1. 服务端（Job执行服务器）随spring启动打开Socket端口；
2. OFFLINE配置执行频率后，到达job执行时间，通过SOCKET与job服务器建立连接；
3. 调用job服务器的execute方法（不等待返回），关闭本次socket连接;

![](https://github.com/jeff-dong/marble/blob/master/document/images/marble_job_dispatch.png) 

<a name="principle_cluster_impl"/>
### Marble实现原理 - 集群支持

TODO

<a name="principle_misfire_impl"/>
### Marble实现原理 - Misfire策略

<a name="development_plan"/>
#后续计划

TODO

<a name="author_about"/>
# 关于作者
Java菜鸟。联系方式djx_19881022@163.com

