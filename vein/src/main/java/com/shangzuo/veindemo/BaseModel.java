package com.shangzuo.veindemo;

import java.io.Serializable;


public class BaseModel<T>  {
    private int cloud;
    private int code; //  0成功  0 失败
    private String msg; //可用来返回接口的说明
    private T data; // 具体的数据结果


    public int getCloud() {
        return cloud;
    }

    public void setCloud(int cloud) {
        this.cloud = cloud;
    }

    public BaseModel(String msg, int code) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseModel{" + "cloud=" + cloud + ", code=" + code + ", msg='" + msg;
    }
}
