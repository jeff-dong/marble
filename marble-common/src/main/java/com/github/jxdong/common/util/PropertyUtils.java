package com.github.jxdong.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
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
        String result =prop.getProperty(key.toLowerCase(), "");
        if(StringUtils.isBlank(result)){
            result =prop.getProperty(key, "");
        }
        return result;
    }

    public static String getKeyToken() {
        return getString("KeyToken");
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
            if(value.trim().equals("true")){
                result = true;
            }
        }
        return result;
    }

    /**
     * 从配置文件中取得vendor的信息，并转化为map
     * @return map
     */
    public static Map<String, String> getVendorInfoMap(){
        Map<String, String> vendorMap = new HashMap<>();
        try{
            String vendorInfo = getString("VendorInfo");
            if(!StringUtils.isBlank(vendorInfo)){
                String[] vendorArray = vendorInfo.split(";");
                if(vendorArray.length >0){
                    for(int i=0; i<vendorArray.length; i++){
                        if(!StringUtils.isBlank(vendorArray[i])){
                            String[] vendorInfoArray = vendorArray[i].split(":");
                            if(vendorInfoArray.length == 2){
                                vendorMap.put(vendorInfoArray[1], vendorInfoArray[0]);
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return vendorMap;
    }

}
