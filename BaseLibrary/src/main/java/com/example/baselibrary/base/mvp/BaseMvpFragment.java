package com.example.baselibrary.base.mvp;

import android.view.View;

import com.example.baselibrary.LibApplication;
import com.example.baselibrary.base.mvc.BaseFragment;
import com.example.baselibrary.dagger.DaggerFragmentComponent;
import com.example.baselibrary.dagger.FragmentComponent;

import javax.inject.Inject;

public abstract class BaseMvpFragment<V extends IBaseView,P extends BasePresenter<V>> extends BaseFragment implements IBaseView {

    @Inject
    protected P mPresenter;
    protected FragmentComponent mFragmentComponent;

    @Override
    protected void init() {
        mFragmentComponent = DaggerFragmentComponent.builder().appComponent(LibApplication.getAppComponent()).build();
        inject();
        mPresenter.attachView((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract void inject();
}
