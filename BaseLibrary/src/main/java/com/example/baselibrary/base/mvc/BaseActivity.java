package com.example.baselibrary.base.mvc;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baselibrary.LibApplication;
import com.example.baselibrary.R;
import com.example.baselibrary.dagger.ActivityComponent;
import com.example.baselibrary.dagger.DaggerActivityComponent;
import com.example.baselibrary.listener.ICancelImmersion;
import com.gyf.immersionbar.ImmersionBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yc.toollib.network.floating.IFloatWindow;

public abstract class BaseActivity extends RxAppCompatActivity {
    private KProgressHUD mProgressDialog;
    protected ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (savedInstanceState != null) {
            onRestartInstance(savedInstanceState);
        }
        mActivityComponent = DaggerActivityComponent
                .builder()
                .appComponent(LibApplication.getAppComponent())
                .build();
        setStatusBarTextBlack();
        initView();
        initData();
        initListener();
    }

    protected abstract int getLayoutId();

    protected abstract void initListener();

    protected abstract void initData();

    protected abstract void initView();

    /**
     * 设置状态栏为白底黑字
     */
    public void setStatusBarTextBlack() {
        if (!(this instanceof ICancelImmersion)) {
            ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).statusBarColor(R.color.color_white).fitsSystemWindows(true).init();
        }
    }

    /**
     * 发生一些意外崩溃，保存一些较小的数据
     * @param savedInstanceState
     */
    protected void onRestartInstance(Bundle savedInstanceState){

    }

    public void showProgress() {
        showProgress(null, true);
    }

    public void showProgress(String msg) {
        showProgress(msg, true);
    }

    public void showProgress(String msg, boolean cancellable) {
        if (mProgressDialog == null) {
            mProgressDialog = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        if (!TextUtils.isEmpty(msg)){
            mProgressDialog.setLabel(msg);
        }
        mProgressDialog.setCancellable(cancellable);

        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
