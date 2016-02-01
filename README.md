
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
1. 引入相关Jar包(Netty等)
1. 定义Job的具体实现类（Spring中的Bean）
1. Spring中配置Job的实现类

OFFLINE端=>
1. 配置对应应用和Job信息（配合权限管理）

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
<marble:scheduler/>: 计划任务配置。一个应用可以配置多个计划任务，每个计划任务暴露在一个IP的端口下，一个计划任务中可以包含多个Job。
| 属性        |    是否必填      | 描述   |
| ------------- |:-------------:| -----:|
| id | 必填 | ID属性，英文+数字的组合来定义该计划任务的唯一标识。|
| host | 非必填      |   所在服务器的IP地址。指定后Marble会尝试将服务暴露在该IP下。不是必填项。由于集群，建议该字段不填，Marble默认会自动获取； |
| zebra stripes | are neat      |    $1 |
| port| 必填| 计划任务暴露的端口号。指明该计划任务暴露在机器的哪个端口下，Marble随Spring启动后会尝试打开本机的该端口并暴露服务。|
| appCode |（必填） | 所在应用的携程APPID。为了在同一台机器上区别不同的应用，以免调用时混淆。|

<marble:job/>: 计划任务下的job的配置。指明具体任务的别名以及实现类。
| 属性        |    是否必填      | 描述   |
| ------------- |:-------------:| -----:|
| name | 必填 | 给当前Job起的别名，在所属的scheduler下必须唯一。|
| description | 非必填 | ob的描述信息，仅用来便于开发者自己识别，非必填。|
| ref | 必填 | Job的具体实现类，必须指向存在的一个Spring Bean。值为bean的name。且该bean必须继承自MarbleJob并覆盖execute方法。Job被调用时会执行execute方法，因此执行逻辑要写在execute中。|



####接入Marble - OFFLINE端
| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

# 关于作者
Java菜鸟。联系方式djx_19881022@163.com
