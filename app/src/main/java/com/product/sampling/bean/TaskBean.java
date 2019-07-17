package com.product.sampling.bean;

import java.util.List;

public class TaskBean {
    private int pageNo = 1;
    private int pageSize = 15;
    private int count = 1;
    List<TaskEntity> list;
    private String html;
    private int firstResult = 15;
    private int maxResults = 1;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<TaskEntity> getList() {
        return list;
    }

    public void setList(List<TaskEntity> list) {
        this.list = list;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}

