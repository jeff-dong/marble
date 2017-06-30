package com.github.jxdong.marble.domain.repositories;

import com.github.jxdong.marble.domain.model.Configure;
import com.github.jxdong.marble.domain.model.Page;
import com.github.jxdong.marble.domain.model.Result;

import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/14 14:04
 */
public interface ConfigureRepository extends Repository {

    Configure queryConfigureById(int id);

    List<Configure> queryConfigureMultiConditions(String[] groups, String[] keys, String orderColumn, String orderDir, Page page);

    Result updateConfigure(Configure configure);

    Result insertConfigure(Configure configure);

    Result deleteConfigureById(int id);

}