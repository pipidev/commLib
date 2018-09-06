package com.dev.pipi.commlib.base.mvp;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : pipi
 *     e-mail : xxx@xx
 *     time   : 2018/04/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public abstract class BasePrsenter<T extends IView> implements IPresenter<T>{
    private WeakReference<T> mViewRef;
    @Override
    public void attachView(T view) {
        mViewRef = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (isAttach()) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public T getView() {
        return isAttach() ? mViewRef.get() : null;
    }
    protected boolean isAttach() {
        return mViewRef != null && mViewRef.get() != null;
    }
}
