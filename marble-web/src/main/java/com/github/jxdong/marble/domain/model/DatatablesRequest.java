package com.github.jxdong.marble.domain.model;


import com.github.jxdong.marble.common.util.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/1/14 13:17
 */
public class DatatablesRequest {

    //原样前端返回
    private int draw;
    //开始记录
    private int start = 0;
    //每页记录数
    private int length = 10;
    //总记录数
    private int recordsTotal = 1;
    //过滤后记录数
    private int recordsFiltered = 1;

    private Search search;

    //排序相关【column:排序列的序号；dir:正序或者倒序】
    private List<Order> order;

    private List<Column> columns;

    private Page page;

    public Page getPage() {
        if (this.page == null) {
            this.page = new Page();
            page.setStartOffset(this.getStart());
            page.setPageSize(this.getLength());
            page.setDraw(this.getDraw());
        }
        return page;
    }
    //获得排序信息e.g: order by orderId asc.目前只支持单列排序
    public String getOrderDir(){
        String dir = "ASC";
        List<Order> orderList = this.getOrder();
        if(ArrayUtils.listIsNotBlank(orderList)){
            dir = orderList.get(0) == null? "ASC":orderList.get(0).getDir();
            if(!dir.equalsIgnoreCase("asc") && !dir.equalsIgnoreCase("desc")){
                dir = "ASC";
            }
        }
        return dir;
    }

    //从search中取得，没有返回-1
    public String getPrimaryKey() {
        if(this.getSearch() != null && StringUtils.isNotBlank(this.getSearch().getValue())){
            try{
                return String.valueOf(this.getSearch().getValue());
            }catch (Exception e){}
        }
        return null;
    }

    //从search中取得，没有返回-1
    public int getIntPrimaryKey() {
        if(this.getSearch() != null && StringUtils.isNotBlank(this.getSearch().getValue())){
            try{
                return Integer.valueOf(this.getSearch().getValue());
            }catch (Exception e){}
        }
        return -1;
    }


    public void setPage(Page page) {
        this.page = page;
    }

    public DatatablesRequest(){

    }
    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }


    public static class Search {
        private String value;
        private boolean regex;

        public Search(){

        }
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isRegex() {
            return regex;
        }

        public void setRegex(boolean regex) {
            this.regex = regex;
        }
    }
    public static class Column {

        private String data;
        private String name;
        private boolean searchable;
        private boolean orderable;
        private Search search;

        public Column(){

        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSearchable() {
            return searchable;
        }

        public void setSearchable(boolean searchable) {
            this.searchable = searchable;
        }

        public boolean isOrderable() {
            return orderable;
        }

        public void setOrderable(boolean orderable) {
            this.orderable = orderable;
        }

        public Search getSearch() {
            return search;
        }

        public void setSearch(Search search) {
            this.search = search;
        }
    }

    public static class Order {
        private String column;
        private String dir;

        public Order(){

        }
        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
}


