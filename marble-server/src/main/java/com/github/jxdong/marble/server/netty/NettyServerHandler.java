package com.github.jxdong.marble.server.netty;

import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.entity.ClassInfo;
import com.github.jxdong.marble.entity.MarbleRequest;
import com.github.jxdong.marble.entity.MarbleResponse;
import com.github.jxdong.marble.entity.ResultCodeEnum;
import com.github.jxdong.marble.server.MarbleJob;
import com.github.jxdong.marble.server.MarbleManager;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2016/1/14 16:06
 */
public class NettyServerHandler extends ChannelHandlerAdapter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    public NettyServerHandler() {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MarbleRequest request = (MarbleRequest)msg;
        logger.info("Receive the marble request: {}", request);

        //默认执行成功
        Boolean operSuccess = true;
        //执行结果字符串
        StringBuilder resultSB = new StringBuilder();

        Set<ClassInfo> classInfoSet = request.getClasses();
        if(classInfoSet != null && classInfoSet.size()>0){
            //建立线程池准备执行
            ExecutorService executor = Executors.newFixedThreadPool(classInfoSet.size());

            for(final ClassInfo classInfo : classInfoSet){
                if(classInfo != null && StringUtils.isNotBlank(classInfo.getClassName())){
                    MarbleJob marbleJob = MarbleManager.getInstance().getMarbleJobByKey(classInfo.getClassName());
                    if(marbleJob == null){
                        logger.warn("Cannot find the MarbleJob-{} from cache.", classInfo.getClassName());
                        continue;
                    }
                    Map<String, Object> result = executeSpringBean(executor, marbleJob, classInfo);
                    operSuccess = (Boolean)result.get("RESULT") ? operSuccess:false;
                    resultSB.append(result.get("RESULT_MSG"));
                }
            }
            //关闭线程池
            executor.shutdown();
        }else{
            logger.warn("Cannot get any Class Info from request.");
        }

        MarbleResponse response = new MarbleResponse(operSuccess? ResultCodeEnum.SUCCESS:ResultCodeEnum.OTHER_ERROR, resultSB.toString());

        //如果需要回写，返回执行结果
        if(request.isNeedResponse()){
            //服务端回写
            ctx.writeAndFlush(response);
        }
        logger.info("Deal with the Marble request result: {}", response);
        ctx.close();
    }

    /**
     * 执行类的某个方法，返回执行结果字符串
     * @param executor 线程池executor
     * @param marbleJob marbleJob对象
     * @param classInfo class对象信息，默认执行execute方法
     * @return Map 执行结果[RESULT_MSG : RESULT]
     */
    private Map<String, Object> executeSpringBean(ExecutorService executor, final MarbleJob marbleJob, final ClassInfo classInfo){
        Map<String, Object> operResult = new HashMap<>();

        boolean isSuccess = true;
        StringBuilder resultSB = new StringBuilder();
        resultSB.append(classInfo.getClassName());
        resultSB.append(": ");

        try{
            //开线程去执行，无须等待执行完成
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    marbleJob.execute(classInfo.getMathodParam());
                }
            });
            resultSB.append("SUCCESS");
        }catch (Exception e){
            isSuccess = false;
            logger.error("Execute Class[{}] exception. ", classInfo, e);
            resultSB.append("FAILED[");
            resultSB.append(e.getMessage());
            resultSB.append("]");
        }
        resultSB.append("; ");

        operResult.put("RESULT_MSG", resultSB.toString());
        operResult.put("RESULT", isSuccess);

        return operResult;
    }

}
