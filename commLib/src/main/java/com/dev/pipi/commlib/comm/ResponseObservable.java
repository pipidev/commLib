package com.dev.pipi.commlib.comm;

import com.dev.pipi.commlib.base.BaseActivity;
import com.dev.pipi.commlib.base.BaseFragment;
import com.dev.pipi.commlib.base.mvp.IView;
import com.dev.pipi.commlib.util.RxUtils;
import com.dev.pipi.commui.statelayout.StateLayout;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * <pre>
 *     author : pipi
 *     e-mail : xxx@xx
 *     time   : 2018/04/04
 *     desc   :使用  new ResponseObservable<>(myModel.getPersonalInfo(), view)
 * .subscribe(new ResponseObserver<BaseData<User>>() {}
 *     version: 1.0
 * </pre>
 */

public class ResponseObservable<T> {
    private Observable<T> observable;
    private IView view;
    private boolean isShowProgress;

    public ResponseObservable(Observable<T> observable, IView view) {
        this.observable = observable;
        this.view = view;
    }

    public ResponseObservable(Observable<T> observable, IView view, boolean isShowProgress) {
        this.observable = observable;
        this.view = view;
        this.isShowProgress = isShowProgress;
    }

    public void subscribe(CustomerObserver<T> observer) {
        convert().subscribe(observer);
    }

    /**
     * view绑定的视图是baseActvity或者BaseFragment
     *
     * @return Observable
     */
    public Observable<T> convert() {
        if (view == null) {
            return observable.doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) throws Exception {
                    disposable.dispose();
                }
            });
        }
        LifecycleTransformer<T> transformer;
        if (view instanceof BaseFragment) {
            transformer = ((BaseFragment) view).bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        } else {
            transformer = ((BaseActivity) view).bindUntilEvent(ActivityEvent.DESTROY);
        }
        return observable
                .compose(RxUtils.<T>schedulersTransformer())
                .compose(transformer)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if (isShowProgress) {
                            view.showLoading();
                        }
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (isShowProgress) {
                            view.hideLoading();
                        }
                    }
                });
    }
}
