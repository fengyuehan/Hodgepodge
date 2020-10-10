package com.example.baselibrary.base.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.baselibrary.base.mvc.BaseActivity;

import javax.inject.Inject;

public abstract class BaseMvpActivity<V extends IBaseView,P extends BasePresenter<V>> extends BaseActivity implements IBaseView {

    @Inject
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        mPresenter.attachView((V) this);
    }

    protected abstract void inject();

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void closeLoading() {
        hideProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
