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

# Marble使用
#------- 服务端使用
Spring中添加Job配置。 marble框架中已经自定义了Spring标签<marble ..>方便直接配置。 示例配置如下:
<marble:scheduler id="Scheduler001" port="9091" appCode="8890"> 
  <marble:job name="Job1" description="jobDescription1" ref="marbleJob1"/> 
  <marble:job name="Job2" description="jobDescription2" ref="marbleJob2"/>
</marble:scheduler>
配置解释:
配置了一个名称为Scheduler001的计划任务(job组),在9091端又上暴露服务。 Scheduler001下包含了两个Job:job1和job2.
Job1的实现类是 Spring bean - marbleJob1;
Job2的实现类是Spring Bean – marbleJob2

Marble标签详解:
<marble:scheduler/>: 计划任务配置。一个应用可以配置多个计划任务,每个计划任务暴露在一个IP的端又下,一个计划任务中可以包含多个Job。
id(必填):ID属性,英文+数字的组合来定义该计划任务的唯一标识。
host(非必填):所在服务器的IP地址。指定后Marble会尝试将服务暴露在该IP下。不是必 填项。由于集群,建议该字段不填,Marble默认会自动获取;
port(必填):计划任务暴露的端又号。指明该计划任务暴露在机器的哪个端又下,Marble 随Spring启动后会尝试打开本机的该端又并暴露服务。
appCode(必填):所在应用的APPID。为了在同一台机器上区别不同的应用,以免调 用时混淆。
<marble:job/>: 计划任务下的job的配置。指明具体任务的别名以及实现类。 name(必填):给当前Job起的别名,在所属的scheduler下必须唯一。 description(非必填):Job的描述信息,仅用来便于开发者自己识别,非必填。
ref(必填):Job的具体实现类,必须指向存在的一个Spring Bean。值为bean的name。且该 bean必须继承自MarbleJob并覆盖execute方法。Job被调用时会执行execute方法,因此执 行逻辑要写在execute中。

#---------OFFLINE使用
TODO

# 关于作者
Java菜鸟。联系方式djx_19881022@163.com
