package com.dev.pipi.commlib.base;

/**
 * <pre>
 *     author : pipi
 *     e-mail : xxx@xx
 *     time   : 2018/04/04
 *     desc   : 返回数据封装成公共类,项目字段不同需要改动(gson修改字段配合改动)
 *     version: 1.0
 * </pre>
 */

public class BaseData<T> {
    private T data;
    private String msg;
    private int status;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
