package com.github.jxdong.marble.infrastructure.repositories.mapper.mysql;

import com.github.jxdong.marble.domain.model.Configure;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

public interface ConfigureMapper {

    List<Configure> selectConfigureByMultiConditions(Map<String, Object> paramMap) throws DataAccessException;

    Configure selectConfigureById(@Param("id") int id) throws DataAccessException;

    int updateConfigureById(Configure configure) throws DataAccessException;

    int insertConfigure(Configure configure) throws DataAccessException;

    int deleteConfigureById(@Param("id") int id) throws DataAccessException;
}
