package com.github.jxdong.marble.global.util;

import com.github.jxdong.marble.domain.model.AppDetail;
import com.github.jxdong.marble.domain.model.Configure;
import com.github.jxdong.marble.domain.model.LoginAccount;
import com.github.jxdong.marble.domain.model.enums.ConfigureEnum;
import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.domain.repositories.AppRepository;
import com.github.jxdong.marble.domain.repositories.ConfigureRepository;
import com.github.jxdong.marble.global.exception.MarbleException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/7/2 14:49
 */
public class AuthorityUtil {
    private static Logger logger = LoggerFactory.getLogger(AuthorityUtil.class);

    //TODO 改为自己的登录方式，此处写死
    public LoginAccount getLoginedAccount() {
        LoginAccount account = new LoginAccount();
        account.setEmployee("A00001");
        account.setName("Jeff Dong");
        account.setHasAdminRole(true);
        return account;
    }

    //得到admin列表
    public Set<String> getAdminList() {
        Set<String> admins = new HashSet<>();
        if (adminList == null || adminList.size() == 0) {
            //DB中查询
            ConfigureRepository confRes = (ConfigureRepository) SpringContextUtil.getBean("configureRepositoryImpl");
            List<Configure> configureList = confRes.queryConfigureMultiConditions(new String[]{ConfigureEnum.Group.USER_ROLE.getCode()}, new String[]{ConfigureEnum.ROLE_ADMIN.getCode()}, null, null, null);
            if (configureList != null && configureList.size() == 1) {
                String adminStr = configureList.get(0).getValue();
                if (StringUtils.isNotBlank(adminStr)) {
                    String[] adminArray = adminStr.split(";");
                    for (String str : adminArray) {
                        admins.add(str.toUpperCase());
                    }
                }
            }
        }
        admins.add("A00001");
        return admins;
    }

    /**
     * 校验userName是否有authorityCode权限
     *
     * @param appCode 应用Code
     * @return boolean 默认false
     */

    public boolean validateAuthority(String appCode) throws MarbleException {
        LoginAccount account = getLoginedAccount();
        if (account == null || StringUtils.isBlank(account.getEmployee())) {
            logger.warn("Validate failed. The param is invalid. employee:{}", account);
            throw new MarbleException(ErrorEnum.ILLEGAL_ARGUMENT, "");
        }
        if (account.getHasAdminRole()) {
            return true;
        }
        String employeeID = account.getUpperEmployee();
        //查找DB中对应的app的owner
        AppRepository appRepository = (AppRepository) SpringContextUtil.getBean("appRepositoryImpl");
        AppDetail app = appRepository.queryAppByCode(appCode);
        if (app == null) {
            logger.warn("There is no app with code({})", appCode);
            throw new MarbleException(ErrorEnum.ILLEGAL_ARGUMENT, "Cannot find the App with code-" + appCode);
        }

        if (employeeID.equals(app.getOwner())) {
            return true;
        } else {
            throw new MarbleException(ErrorEnum.NO_PERMISSION, "");
        }
    }

    //取得本机IP
    public static String getLocalIPAddress() {
        String ipAddress = "";
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        if(!"127.0.0.1".equals(ip.getHostAddress())){
                            ipAddress = ip.getHostAddress();
                            logger.info("IP address :{}", ip.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("get local ip address exception. ", e);
        }
        return ipAddress;
    }


    //清空缓存
    public void clear() {
        this.adminList = null;
    }

    //单例模式实现
    private static class SingletonHolder {
        private static final AuthorityUtil instance = new AuthorityUtil();
    }

    private AuthorityUtil(){
    }

    public static AuthorityUtil getInstance() {
        return SingletonHolder.instance;
    }

    private Set<String> adminList;
}
