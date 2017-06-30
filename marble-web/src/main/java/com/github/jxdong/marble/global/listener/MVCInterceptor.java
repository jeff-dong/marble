package com.github.jxdong.marble.global.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/12/11 13:56
 */
public class MVCInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MVCInterceptor.class);

    /**
     * 该方法将在Controller处理之前进行调用
     * 1、判断是否为HULK系统调用；
     * 2、判断有没有登录；
     * 3、参数进行sql+html+js过滤
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @param o 参数
     * @return 返回 false表示结束
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        //登陆状态校验
        if(httpServletRequest.getSession() == null){
            logger.warn("not login. request failed !");
            return false;
        }

        return true;
    }

    /**
     * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之
     * 后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操作
     ** @param httpServletRequest request
     * @param httpServletResponse response
     * @param o 参数
     * @param modelAndView 视图
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行，
     * 这个方法的主要作用是用于清理资源的，当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

//    private String printRequest(HttpServletRequest req){
//        StringBuffer sb = new StringBuffer();
//        sb.append("Remote User: ");sb.append(req.getRemoteUser()); sb.append(". ");
//        sb.append("Remote address: "); sb.append(req.getRemoteAddr()); sb.append(". ");
//        sb.append("Session: "); sb.append(req.getSession() != null ? req.getSession() : "");
//
//        sb.append(". Headers: ");
//        Enumeration<String> en = req.getHeaderNames();
//        String header="";
//        while(en.hasMoreElements()){
//            header = en.nextElement();
//            sb.append(header);
//            sb.append(": ");
//            sb.append(req.getHeader(header));
//            sb.append(", ");
//        }
//        return sb.toString();
//
//    }
}
