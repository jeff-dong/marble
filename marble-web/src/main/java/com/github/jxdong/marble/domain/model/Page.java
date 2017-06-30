package com.github.jxdong.marble.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对分页的基本数据进行一个简单的封装
 */
public class Page<T>{

    private int draw;//自增长
    private int currentPage = 1;// 页码，默认是第一页
    private int startOffset = 0;//开始记录
    private int pageSize = 10;// 每页显示的记录数，默认是10
    private int totalRecord;// 总记录数
    private int totalPage = 1;// 总页数
    private List<T> results;// 对应的当前页记录
    private Map<String, Object> params = new ConcurrentHashMap<>();// 其他的参数我们把它分装成一个Map对象

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    //得到起始记录值的recordID
    public int getStartOffset(){
        return  startOffset;//(this.currentPage - 1) * this.pageSize;
    }
    private List<?> basePojoList;

    //根据startOffset 和 pageSize得到当前页码
    public int getCurrentPage() {
        if(this.pageSize >0){
            return startOffset < pageSize? 1: (startOffset/pageSize + 1);
        }else {
            return 1;
        }
    }

    public Page() {
    }

    public Page(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        // 在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
        int totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize
                : totalRecord / pageSize + 1;
        this.setTotalPage(totalPage);
    }


    public List<?> getBasePojoList() {
        return basePojoList;
    }

    public void setBasePojoList(List<?> basePojoList) {
        this.basePojoList = basePojoList;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        if (totalPage <= 0) {
            totalPage = 1;
        }
        this.totalPage = totalPage;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Page [currentPage=").append(currentPage)
                .append(", pageSize=").append(pageSize).append(", results=")
                .append(results).append(", totalPage=").append(totalPage)
                .append(", totalRecord=").append(totalRecord).append("]");
        return builder.toString();
    }

    /**
     * 从分页插件的page中得到total rocord
     * @param list
     */
    @JsonIgnore
    public void setTotalRecord(List<T> list){
        if(list != null ){
            if(list instanceof com.github.pagehelper.Page){
                this.setTotalRecord((int) ((com.github.pagehelper.Page) list).getTotal());
            }else{
                this.setTotalRecord(list.size());
            }
        }
    }
}