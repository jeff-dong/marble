package com.github.jxdong.marble.agent.common.server.netty.server;

import com.github.jxdong.marble.agent.common.server.MarbleJob;
import com.github.jxdong.marble.agent.common.server.MarbleManager;
import com.github.jxdong.marble.agent.common.server.global.MarbleThread;
import com.github.jxdong.marble.agent.common.util.JsonUtil;
import com.github.jxdong.marble.agent.common.util.StringUtils;
import com.github.jxdong.marble.agent.entity.*;
import com.github.jxdong.marble.agent.common.util.ClogWrapper;
import com.github.jxdong.marble.agent.common.util.ClogWrapperFactory;
import com.github.jxdong.marble.agent.common.server.global.MarbleJobPool;
import com.github.jxdong.marble.agent.common.server.global.ThreadPool;
import com.github.jxdong.marble.agent.common.server.netty.client.NettyClientManager;
import com.google.common.base.Throwables;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/1/14 16:06
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static ClogWrapper logger = ClogWrapperFactory.getClogWrapper(NettyServerHandler.class);

    public NettyServerHandler() {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final MarbleRequest request = (MarbleRequest) msg;
        String reqNo = request.getRequestNo();
        logger.REQNO(reqNo).info("Netty received the marble request: {}", JsonUtil.toJsonString(request));
        MarbleResponse response = new MarbleResponse(request.getRequestNo(), ResultCodeEnum.OTHER_ERROR, "未知错误");
        try {
            //默认执行成功
            Result execResult = Result.SUCCESS();
            Map<String, Object> data = request.getData();

            //根据操作ID判断操作类型
            if (data.get("OPERATE_ID") != null && "A00002".equalsIgnoreCase(data.get("OPERATE_ID").toString())) {
                if(data.get("JOB_ID") == null){
                    execResult = Result.FAILURE("Marble执行失败：参数未传递JOB_ID信息");
                }else{
                    ThreadPool.getFixedInstance().stopJobThread(StringUtils.safeString(data.get("OPERATOR")), data.get("JOB_ID").toString());
                }
            } else {
                if (data.get("CLASS_INFO") != null) {
                    Set<ClassInfo> classInfoSet = (Set<ClassInfo>) data.get("CLASS_INFO");
                    if (classInfoSet != null && classInfoSet.size() > 0) {
                        final boolean isSync = data.get("JOB_IS_SYNC") != null && (boolean) data.get("JOB_IS_SYNC");
                        final Long maxWaitTime = (Long) data.get("JOB_MAX_WAIT_TIME");
                        final String serverIpInfo = data.get("IP_INFO") == null ? null : String.valueOf(data.get("IP_INFO"));

                        //是否执行成功，只要有一个失败就算失败
                        for (final ClassInfo classInfo : classInfoSet) {
                            if (classInfo == null || StringUtils.isBlank(classInfo.getClassName())) {
                                continue;
                            }
                            final MarbleJob marbleJob = MarbleManager.getInstance().getMarbleJobByKey(classInfo.getClassName());
                            if (marbleJob == null) {
                                logger.REQNO(reqNo).error("Cannot find the MarbleJob-{} from cache.", classInfo.getClassName());
                                execResult.setResultCode(20);
                                execResult.setResultMsg("失败");
                                execResult.putData(classInfo.getClassName(), Result.FAILURE(JobExecStatusEnum.FAILURE.getCode(), "Marble找不到其实现类(" + classInfo.getClassName() + ")，无法执行"));
                                continue;
                            }

                            //同步的JOB，需要返回请求中
                            if (isSync) {
                                execResult = Result.PROCESSING();
                                ThreadPool.getFixedInstance().execute(request.getRequestNo(), new Runnable() {
                                    @Override
                                    public void run() {
                                        executeSpringBean(request.getRequestNo(), marbleJob, classInfo, true, maxWaitTime, serverIpInfo);
                                    }
                                });
                            } else {
                                execResult = executeSpringBean(request.getRequestNo(), marbleJob, classInfo, isSync, maxWaitTime, serverIpInfo);
                            }
                            execResult.putData(classInfo.getClassName(), execResult);
                        }
                    }
                } else {
                    logger.REQNO(reqNo).warn("cannot get any Class Info from request.");
                    execResult = Result.FAILURE("Marble执行失败：没有收到要执行的类信息");
                    // execResult.putData("EXEC_FAILED", "Marble执行失败：没有收到要执行的类信息");
                }
            }
            response = new MarbleResponse(request.getRequestNo(), execResult.isSuccess() ? ResultCodeEnum.SUCCESS : ResultCodeEnum.OTHER_ERROR, JsonUtil.toJsonString(execResult));
        } catch (Exception e) {
            logger.REQNO(reqNo).error("Netty server exec job exception, detail: {}", Throwables.getStackTraceAsString(e));
            response = new MarbleResponse(request.getRequestNo(), ResultCodeEnum.OTHER_ERROR, "异常：" + e.getMessage());
        } finally {
            //如果需要回写，返回执行结果
            if (request.isNeedResponse()) {
                logger.REQNO(reqNo).info("try to write back response");
                //服务端回写
                ctx.writeAndFlush(response);
            }
            logger.REQNO(reqNo).info("marble exec end. response: {}", JsonUtil.toJsonString(response));
            ctx.close();
        }

    }

    /**
     * 执行类的某个方法，返回执行结果字符串
     *
     * @param marbleJob     marbleJob对象
     * @param classInfo     class对象信息，默认执行execute方法
     * @param isSynchronous 是否同步返回结果
     * @param maxWaitTime   最长等待时间（分钟）
     * @return Map 执行结果[RESULT_MSG : RESULT]
     */
    private Result executeSpringBean(final String reqNo, final MarbleJob marbleJob, final ClassInfo classInfo, final boolean isSynchronous, Long maxWaitTime, String serverIpInfo) {
        String className = classInfo.getClassName();
        logger.REQNO(reqNo).info("Spring execute job, requestNo:{}, className:{}, isSynchronous:{}, maxWaitTime:{}", reqNo, className, isSynchronous, maxWaitTime);

        Result execResult = Result.FAILURE("JOB[" + className + "]执行未知错误");

        //解析服务器信息
        String[] arrayIpInfo = null;
        String operator = "";
        try {
            if (StringUtils.isNotBlank(serverIpInfo)) {
                arrayIpInfo  = serverIpInfo.split(":");
            }
            //同步JOB，需要开启等待
            if (isSynchronous) {
                //放入内存
                if(arrayIpInfo != null){
                    logger.REQNO(reqNo).info("put the job into marble-job-pool, arrayIpInfo:{}-{}", arrayIpInfo[0], arrayIpInfo[1]);
                    MarbleJobPool.getInstance().addProcessingJob(reqNo, true, arrayIpInfo[0], Integer.valueOf(arrayIpInfo[1]));
                }
                execResult = ThreadPool.getFixedInstance().executeWithResult(reqNo, className, marbleJob, classInfo.getMathodParam(), maxWaitTime);
            } else {
                MarbleThread marbleThread = new MarbleThread(marbleJob, classInfo.getMathodParam());
                //非同步JOB, 直接返回结果
                ThreadPool.getFixedInstance().execute(reqNo, marbleThread);
                ThreadPool.getFixedInstance().multimapPut(className, marbleThread);
                logger.REQNO(reqNo).info("put the thread into ThreadMultiMap, className:{}", reqNo, className);
                execResult = Result.SUCCESS(ThreadPool.getFixedInstance().queueContainThread(marbleThread)?"进入阻塞队列等待执行":"成功");
            }
        } catch (TimeoutException e) {
            logger.REQNO(reqNo).error("job execute[{}] timeoutException. detail:{}", classInfo, Throwables.getStackTraceAsString(e));
            execResult = Result.FAILURE(className + " 执行失败, 原因: Marble等待[" + (maxWaitTime == null ? "10" : maxWaitTime) + "]分钟后未收到执行结果超时");
        }catch (RejectedExecutionException e){
            logger.REQNO(reqNo).error("Execute Class[{}] exception. ThreadPool is full, detail:{}", classInfo, Throwables.getStackTraceAsString(e));
            execResult = Result.FAILURE(className + " 执行失败, 原因:线程池已满(" + ThreadPool.getFixedInstance().getPoolDescInfo()+")");
        }catch (Exception e) {
            logger.REQNO(reqNo).error("Execute Class[{}] exception. detail:{}", classInfo, Throwables.getStackTraceAsString(e));
            String msg = e.getMessage();
            if(e instanceof java.util.concurrent.CancellationException){
                msg = "线程被中断";
            }
            execResult = Result.FAILURE(className + " 执行失败, 原因: " + msg);
        } finally {
            //executor.shutdown();
            logger.REQNO(reqNo).info("job({}) execute end, result: {}", className, execResult);
            //通知状态
            Map<String, Object> data = new HashMap<>();
            data.put("EXEC_RESULT", execResult);
            if (arrayIpInfo != null) {
                logger.REQNO(reqNo).info("job execute end, try to notify the Marble exec result");
                NettyClientManager.getInstance().serviceInvoke(reqNo, arrayIpInfo[0], Integer.valueOf(arrayIpInfo[1]), data);
                //内存移除
                MarbleJobPool.getInstance().removeProcessingJob(reqNo);
            }
        }

        return execResult;
    }

}
