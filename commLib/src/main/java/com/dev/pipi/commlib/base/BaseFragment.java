package com.dev.pipi.commlib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.dev.pipi.commfunc.multimedia.MultimediaUtils;
import com.dev.pipi.commlib.R;
import com.dev.pipi.commlib.comm.EventCode;
import com.dev.pipi.commlib.comm.EventMsg;
import com.dev.pipi.commlib.comm.PermissionCode;
import com.dev.pipi.commlib.comm.Permissions;
import com.dev.pipi.commlib.util.EventBusUtils;
import com.dev.pipi.commlib.util.PermissionUtils;
import com.dev.pipi.commui.CircleDialog;
import com.dev.pipi.commui.CustomerDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.zhihu.matisse.Matisse;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * <pre>
 *     author : pipi
 *     e-mail : xxx@xx
 *     time   : 2018/03/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public abstract class BaseFragment extends RxFragment implements EasyPermissions.PermissionCallbacks{
    protected Context mContext;
    protected View mRootView;
    private boolean isVisible = false;//当前Fragment是否可见
    private boolean isInitView = false;//view是否初始化完毕
    private boolean isFirstLoad = true;//是否是第一次加载数据
    private CustomerDialog mCustomerDialog;
    private CircleDialog mCircleDialog;
    private boolean isCreateNew = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isCreateNew()|| mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        ButterKnife.bind(this, mRootView);
        setHasOptionsMenu(true);
        return mRootView;
    }

    public boolean isCreateNew() {
        return isCreateNew;
    }
    protected void showCustomerLoading() {
        if (mCustomerDialog == null) {
            mCustomerDialog = new CustomerDialog();
        }
        if (!mCustomerDialog.isAdded()) {
            mCustomerDialog.show(getChildFragmentManager(),BaseFragment.class.getSimpleName());
        }
    }
    protected void hideCustomerLoading() {
        if (mCustomerDialog != null && mCustomerDialog.isAdded()) {
            mCustomerDialog.dismiss();
        }
    }
    protected void showCircleLoading() {
        if (mCircleDialog == null) {
            mCircleDialog = new CircleDialog();
        }
        if (!mCircleDialog.isAdded()) {
            mCircleDialog.show(getChildFragmentManager(),BaseActivity.class.getSimpleName());
        }
    }
    protected void hideCircleLoading() {
        if (mCircleDialog != null && mCircleDialog.isAdded()) {
            mCircleDialog.dismiss();
        }
    }
    private void lazyLoadData() {
        if (!isFirstLoad || !isVisible || !isInitView) {
            return;
        }
        loadData();
        isFirstLoad = false;
    }

    /**
     * 懒加载
     */
    protected void loadData() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        isInitView = true;
        lazyLoadData();
    }

    protected abstract int getLayoutId();

    protected abstract void init();
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoadData();

        } else {
            isVisible = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
    protected void back() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.popBackStackImmediate()) {
        } else {
            getActivity().finish();
        }
    }
    //权限处理 AfterPermissionGranted中一定要做处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    /**
     * 此方法为了解决easypermission申请多条时的权限bug
     * 申请多条时权限1同意,权限2拒绝.再次请求时如果权限1点拒绝就会崩溃
     * 这时只需要申请权限2
     * 以下参数同EasyPermissions
     * @param rationale
     * @param requestCode
     * @param perms
     */
    protected void requestPermissions(@NonNull String rationale,
                                      int requestCode, @NonNull String... perms){
        PermissionUtils.requestPermissions(this,rationale,requestCode,perms);
    }
    protected boolean hasPermission(@NonNull String... perms) {
        return EasyPermissions.hasPermissions(mContext,perms);
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(this);
            AppSettingsDialog dialog = builder.setTitle(getString(R.string.request_permission))
                    .setRationale(getString(R.string.permissions_requests))
                    .build();
            dialog.show();
        } else {
            ToastUtils.showShort(getString(R.string.requests_deny));
            doOnPermissionDeny();
        }
    }

    protected void doOnPermissionDeny() {

    }
    protected void cancelRefresh(SmartRefreshLayout refreshLayout) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.finishRefresh();
        } else if (refreshLayout.isLoading()) {
            refreshLayout.finishLoadmore();
        }
    }

    protected void initRefreshLayout(SmartRefreshLayout refreshLayout) {
        refreshLayout.setEnableOverScrollDrag(false);
        refreshLayout.setEnableAutoLoadmore(false);
    }

    /**
     * 选择照片
     * 写在这里是处理权限
     */
    @AfterPermissionGranted(PermissionCode.PERMISSION_CODE_PHOTO)
    public void takePhoto() {
        if (hasPermission(Permissions.PERMISSIONS_PHOTO)) {
            MultimediaUtils.takePhoto(this);
        } else {
            requestPermissions(getString(R.string.permission_sd),PermissionCode.PERMISSION_CODE_PHOTO,
                    Permissions.PERMISSIONS_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionCode.PERMISSION_CODE_PHOTO && resultCode == Activity.RESULT_OK) {
            Log.d("Matisse", "Uris: " + Matisse.obtainResult(data));
            Log.d("Matisse", "Paths: " + Matisse.obtainPathResult(data));
            EventBusUtils.post(new EventMsg<>(EventCode.PHOTO_CODE,Matisse.obtainPathResult(data)));
        }
    }
}
