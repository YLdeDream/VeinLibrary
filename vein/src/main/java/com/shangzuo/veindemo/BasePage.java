package com.shangzuo.veindemo;

public class BasePage <T>{
    private int pageSize;
    private int pageIndex;
    private int totalNum;
    private int totalPage;
    private T result; // 具体的数据结果

    public BasePage() {
    }

    public BasePage(int pageSize, int pageIndex, int totalNum, int totalPage, T result) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.totalNum = totalNum;
        this.totalPage = totalPage;
        this.result = result;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
