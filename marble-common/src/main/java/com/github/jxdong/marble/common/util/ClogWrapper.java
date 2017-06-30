package com.github.jxdong.marble.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clog帮助类
 *
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/6/14 16:46
 */
public class ClogWrapper {
    private Logger logger = null;

    //存储当前Clog的tag map列表
    private ThreadLocal tagMapLocal = new ThreadLocal();

    public ClogWrapper(Class clogClass) {
        logger = LoggerFactory.getLogger(clogClass);
        //初始化ThreadLocal
        tagMapLocal.set(new LinkedHashMap<String, Object>());
    }

    private void clearTag() {
        tagMapLocal.remove();
    }

    //日志打印落地
    private void writeClog(LevelEnum level, String message, Object... arguments) {

        StringBuilder sb = new StringBuilder();

        //tag有值，打印tag，然后清空map
        if (tagMapLocal.get() != null) {
            Map<String, Object> tagMap = (Map<String, Object>) tagMapLocal.get();

            if (tagMap.size() > 0) {
                sb.append("[[");
                for (Map.Entry<String, Object> entry : tagMap.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
                }
                sb = sb.replace(sb.length() - 1, sb.length(), "");
                sb.append("]]");
            }
        }

        //拼接 title
        /*
        if (title != null && title.trim().length() > 0) {
            sb.append("[").append(title).append("] ");
        }*/
        //sb.append(title);
        //拼接message
        sb.append(message).append(" ");

        //可变参数转化为String
        String[] strArgs = null;
        if (arguments != null) {
            strArgs = new String[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                strArgs[i] = arguments[i] == null ? "" : arguments[i].toString();
            }
        }

        switch (level) {
            case DEBUG:
                logger.debug(sb.toString(), strArgs);
                break;
            case WARN:
                logger.warn(sb.toString(), strArgs);
                break;
            case ERROR:
                logger.error(sb.toString(), strArgs);
                break;
            default:
                logger.info(sb.toString(), strArgs);
                break;
        }

        clearTag();
    }

    /**
     * 添加标签。格式 tagName=tagValue
     *
     * @return ClogWrapper
     */
    public ClogWrapper addTag(String key, String value) {
        if (key == null || key.trim().length() == 0 || value == null || value.trim().length() == 0) {
            return this;
        }
        Object object = tagMapLocal.get();
        if (object == null) {
            tagMapLocal.set(new LinkedHashMap<String, Object>());
        }

        Map<String, Object> tagMap = (Map<String, Object>) tagMapLocal.get();

        tagMap.put(key, value);
        return this;
    }

    public ClogWrapper MARK(String mark) {
        if (StringUtils.isNotBlank(mark)) {
            addTag("MARK", String.valueOf(mark));
        }
        return this;
    }

    public ClogWrapper REQNO(String reqNo) {
        if (StringUtils.isNotBlank(reqNo)) {
            addTag("REQ_NO", String.valueOf(reqNo));
        }
        return this;
    }

    public ClogWrapper SERVICE(String service) {
        if (StringUtils.isNotBlank(service)) {
            addTag("SERVICE", service);
        }
        return this;
    }

    public ClogWrapper POOL(String flag) {
        if (flag != null && flag.trim().length() > 0) {
            addTag("POOL_MARK", flag);
        }
        return this;
    }

    //tag格式 [key1=value1,key2=value2]
    public ClogWrapper addTag(String tagStr) {
        if (tagStr == null || tagStr.length() == 0) {
            return this;
        }
        String[] tagArray = tagStr.split(",");

        Object object = tagMapLocal.get();
        if (object == null) {
            tagMapLocal.set(new LinkedHashMap<String, Object>());
        }

        Map<String, Object> tagMap = (Map<String, Object>) tagMapLocal.get();

        for (String str : tagArray) {
            String[] subTagArray;
            if (str != null) {
                subTagArray = str.split("=");
                if (subTagArray.length == 2) {
                    tagMap.put(subTagArray[0], subTagArray[1]);
                }
            }
        }

        return this;
    }

    //DEBUG 级别
    public void debug(String message, Object... arguments) {
        writeClog(LevelEnum.DEBUG, message, arguments);
    }

    //INFO 级别
    public void info(String message, Object... arguments) {
        writeClog(LevelEnum.INFO, message, arguments);
    }

    //WARN 级别
    public void warn(String message, Object... arguments) {
        writeClog(LevelEnum.WARN, message, arguments);
    }

    //ERROR 级别
    public void error(String message, Object... arguments) {
        writeClog(LevelEnum.ERROR, message, arguments);
    }

    private enum LevelEnum {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public enum FormatEnum {
        DEF,
        JSON
    }

}




