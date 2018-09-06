package com.dev.pipi.commlib.exception;

import com.dev.pipi.commlib.base.BaseData;

import io.reactivex.functions.Function;

/**
 * <pre>
 *     author : pipi
 *     e-mail : xxx@xx
 *     time   : 2018/04/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ServerResultFunc<T> implements Function<BaseData<T>,T> {

    @Override
    public T apply(BaseData<T> baseData) throws Exception {
        if (baseData.getStatus() != 200) {
            if (baseData.getMsg() == null) {
                throw new ApiException(baseData.getStatus(), "服务器发生错误");
            }
            throw new ApiException(baseData.getStatus(),baseData.getMsg());
        }
        if (baseData.getData() == null) {
            throw new ApiException(800, "暂无数据");
        }
        /*if (baseData.getData() instanceof Collection) {
            Collection data = (Collection) baseData.getData();
            if (data.isEmpty()) {
                throw new ApiException(800, "暂无数据");
            }
        }*/
        return baseData.getData();
    }
}

