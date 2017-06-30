package com.github.jxdong.marble.agent.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/07/01 15:45
 */
public class PropertyUtils {
    public static final String CONFIG = "config.properties";
    private static Properties prop;

    static {
        prop = new Properties();
        try {
            prop.load(PropertyUtils.class.getClassLoader().getResourceAsStream(CONFIG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        String result = prop.getProperty(key.toLowerCase(), "");
        if (StringUtils.isBlank(result)) {
            result = prop.getProperty(key, "");
        }
        return result;
    }

    public static String getCurEnvironment(){
        return getString("RunEnv");
    }

    public static int getMarbleServerPort() {
        return getInteger("MarbleServerPort");
    }

    public static Integer getInteger(String key) {
        String value = PropertyUtils.getString(key);
        Integer result = null;
        if (value != null && value.length() > 0) {
            result = Integer.parseInt(value);
        }
        return result;
    }

    public static Boolean getBoolean(String key) {
        String value = PropertyUtils.getString(key);
        Boolean result = false;
        if (value != null && value.length() > 0) {
            if (value.trim().equals("true")) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取本机ip地址，linux操作系统试用
     *
     * @return String
     */
    public static String getLocalIP() {
        String sIP = "";
        InetAddress ip = null;
        try {
            boolean bFindIP = false;
            Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                if (bFindIP) {
                    break;
                }
                NetworkInterface ni = netInterfaces.nextElement();
                //遍历所有ip
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = ips.nextElement();
                    if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()   //127.开头的都是lookback地址
                            && !ip.getHostAddress().contains(":")) {
                        bFindIP = true;
                        break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != ip) {
            sIP = ip.getHostAddress();
        }
        return sIP;
    }

}
