package com.github.jxdong.marble.global.util;

import com.github.jxdong.marble.common.util.ArrayUtils;
import com.github.jxdong.marble.domain.dto.BaseDTO;
import com.github.jxdong.marble.domain.model.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * 将内部的entity转化为DTO供网络传输
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/26 9:19
 */
public class DTOConvert {
    private static Logger logger = LoggerFactory.getLogger(DTOConvert.class);

    public static BaseDTO entity2DTO(Entity entity, Class<? extends BaseDTO> dtoClass){
        if(entity == null || dtoClass == null ){
            return null;
        }
        BaseDTO dto = null;
        try {
            dto = dtoClass.newInstance();
            BeanCopier bc =BeanCopier.create(entity.getClass(), dtoClass, false);
            bc.copy(entity, dto, null);
            return dto;
        } catch (Exception e) {
            logger.error("convert POJO to DTO exception. detail: ", e);
        }
        return dto;
    }

    public static List<BaseDTO> entity2DTO(List<? extends Entity> entityList, Class<? extends BaseDTO> dtoClass){
        List<BaseDTO> dtoList = new ArrayList<>();
        if(ArrayUtils.listIsNotBlank(entityList)){
            for(Entity entity : entityList){
                if(entity != null){
                    dtoList.add(entity2DTO(entity, dtoClass));
                }
            }
        }
        return dtoList;
    }

}
