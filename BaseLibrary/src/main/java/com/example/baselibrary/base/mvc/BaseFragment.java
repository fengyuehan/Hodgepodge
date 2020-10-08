package com.example.baselibrary.base.mvc;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.baselibrary.base.BaseActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {
    public BaseActivity mActivity;
    public View mRoot;

    //Fragment的View加载完毕的标记
    protected boolean isViewCreated;

    //Fragment对用户可见的标记
    private boolean isUIVisible;
    protected boolean mIsLazyLoaded;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null)
                parent.removeView(mRoot);
        } else {
            mRoot = initView();
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initListener();
        isViewCreated = true;
        mIsLazyLoaded = false;
        lazyLoad();
    }

    private void lazyLoad() {
        /**
         * 这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,
         * 必须确保onCreateView加载完毕且页面可见,才加载数据
         *
         */
        if (isViewCreated && isUIVisible){
            onLazyLoadData();
            isViewCreated = false;
            isUIVisible = false;
            mIsLazyLoaded = true;
        }else if (mIsLazyLoaded && isUIVisible){
            onRefreshData();
        }
    }

    /**
     * 非首次该fragment显示，适合第二次后每次进入页面，刷新数据使用
     */
    protected abstract void onRefreshData();

    /**
     * 第一次进去的时候懒加载数据
     */
    protected abstract void onLazyLoadData();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            isUIVisible = true;
            lazyLoad();
        }else {
            isUIVisible = false;
        }
    }

    protected abstract void initListener();

    protected abstract void initData();

    protected abstract View initView();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }
}
