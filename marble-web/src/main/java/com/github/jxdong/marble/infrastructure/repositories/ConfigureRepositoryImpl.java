package com.github.jxdong.marble.infrastructure.repositories;

import com.github.jxdong.marble.common.util.CommonUtil;
import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.Configure;
import com.github.jxdong.marble.domain.model.Page;
import com.github.jxdong.marble.domain.model.Result;
import com.github.jxdong.marble.domain.repositories.ConfigureRepository;
import com.github.jxdong.marble.infrastructure.repositories.mapper.mysql.ConfigureMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/25 14:18
 */
@Repository
public class ConfigureRepositoryImpl implements ConfigureRepository {
    private static Logger logger = LoggerFactory.getLogger(ConfigureRepositoryImpl.class);
    @Autowired
    private ConfigureMapper configureMapper;


    @Override
    public Configure queryConfigureById(int id) {
        Configure configure = null;
        try{
            configure = configureMapper.selectConfigureById(id);
        }catch (Exception e){
            logger.error("Query configure by primary key({})  error, detail info: ",id, e);
        }
        return configure;
    }

    @Override
    public List<Configure> queryConfigureMultiConditions(String[] groups, String[] keys, String orderColumn, String orderDir, Page page) {
        List<Configure> configureList = null;
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("groups",groups);
            paramMap.put("keys",keys);
            paramMap.put("orderColumn", orderColumn);
            paramMap.put("orderDir", orderDir);
            if(page != null){
                paramMap.put("page", page);
                //分页
                PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
            }
            configureList = configureMapper.selectConfigureByMultiConditions(paramMap);
            if(page != null){
                page.setTotalRecord(configureList);
            }
        }catch (Exception e){
            logger.error("Query configure , detail info: ", e);
        }
        return configureList;
    }


    @Override
    public Result updateConfigure(Configure configure) {
        if(configure == null || configure.getId() <=0){
            return Result.FAILURE("Illegal arguments");
        }
        try {
            Configure oldConfigure = queryConfigureById(configure.getId());
            if(oldConfigure == null){
                return Result.FAILURE("cannot find the configure record");
            }
            boolean need2Update= false;
            if(!oldConfigure.getValue().equals(configure.getValue()) && StringUtils.isNotBlank(configure.getValue())){
                oldConfigure.setValue(configure.getValue());
                need2Update= true;
            }
            if(!StringUtils.safeString(oldConfigure.getDescription()).equals(StringUtils.safeString(configure.getDescription()))){
                oldConfigure.setDescription(configure.getDescription());
                need2Update= true;
            }
            if(need2Update){
                configureMapper.updateConfigureById(oldConfigure);
            }
        }catch (Exception e){
            logger.error("Update Configure({}) error, detail info: ", configure, e);
            return Result.FAILURE("update configure failed");
        }
        return Result.SUCCESS();
    }

    @Override
    public Result insertConfigure(Configure configure) {
        try {
            List<Configure> configureList = this.queryConfigureMultiConditions(new String[]{configure.getGroup()}, new String[]{configure.getKey()}, null, null, new Page());
            if(CommonUtil.listIsNotBlank(configureList)){
                return Result.FAILURE("same record has exist");
            }
            configureMapper.insertConfigure(configure);

        }catch (Exception e){
            logger.error("insert Configure({}) exception, detail info: ", configure, e);
            return Result.FAILURE("insert configure failed. inner error");
        }
        return Result.SUCCESS();
    }

    @Override
    public Result deleteConfigureById(int id) {
        try{
            Configure configure = queryConfigureById(id);
            if(configure == null){
                return Result.FAILURE("cannot find the configure record");
            }
            configureMapper.deleteConfigureById(id);
        }catch (Exception e){
            logger.error("delete Configure(id={}) exception, detail info: ", id, e);
            return Result.FAILURE("delete configure failed. inner error");
        }
        return Result.SUCCESS();
    }

}
