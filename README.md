# Marble是什么
Marble是一款Java实现的计划任务调度框架，包含Marble-OFFLINE和Marble-Agent两部分。
- Marble OFFLINE：JOB调度控制中心，且提供一个可视化的计划任务的操作管理页面；
- Marble Agent：以Jar包形式整合进接入Marble的第三方应用，透明化Job的调度过程；

# Marble能做什么
1. Java JOB（同步JOB和异步JOB）调度；
2. JOB参数支持;
3. OFFLINE管理平台进行JOB管理;
4. 支持手动JOB调用;
5. 基本的权限控制;
6. JOB线程中断支持；
7. JOB执行全程追踪；

# Marble快速接入教程
## 1 Marble Jar包引入
```
<dependency>
    <groupId>com.github.jeff-dong.marble</groupId>
    <artifactId>marble-agent</artifactId>
    <version>2.0.0</version>
</dependency>
```
## 2 配置Marble（与Spring整合）
```
<!-- 配置Marble job. 如果有多个，可配置多个<marble:job ..>标签 -->
<marble:scheduler id="计划任务名称" port="可用的端口号" appCode="应用的AppCode(唯一标识)">
    <marble:job name="JOB名称" ref="执行JOB的SpringBean名称"/>
    ...
</marble:scheduler>
```
## 3 自定义实现类（JOB执行逻辑）
异步JOB
```
@Component
public class TestMarbleJob1 extends MarbleJob {
    private Logger logger = LoggerFactory.getLogger(TestMarbleJob1.class);
    @Override
    public void execute(String param) {
        logger.info(" 异步 JOB1 执行正常： {}", param);
    }
}
```
同步JOB
```
@Component
public class TestSyncMarbleJob1 extends MarbleJob {
    private Logger logger = LoggerFactory.getLogger(TestSyncMarbleJob1.class);
 
    @Override
    public Result executeSync(String param) {
        logger.info(" 同步 JOB1(3S) 执行正常： {}", param);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.SUCCESS();
    }
}
```

##4 OFFLINE配置JOB执行频率

![image.png](https://github.com/jeff-dong/marble/blob/master/document/resource/image2016-9-23%209-33-3.png)
![image.png](https://github.com/jeff-dong/marble/blob/master/document/resource/image2016-9-23%209-40-50.png)
