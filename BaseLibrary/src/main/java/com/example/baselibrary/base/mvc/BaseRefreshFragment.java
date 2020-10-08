package com.example.baselibrary.base.mvc;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.baselibrary.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public abstract class BaseRefreshFragment<T> extends BaseFragment {
    protected int mPageIndex = 1;
    protected String mSize = "20";
    protected SmartRefreshLayout mRefreshLayout;
    private BaseQuickAdapter mAdapter;
    private List<T> mData;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSmartRefreshLayout();
    }

    private void initSmartRefreshLayout() {
        mRefreshLayout = setRefreshLayout();
        mAdapter = setRefreshAdapter();
        setSmartRefreshLayoutListener();
    }

    protected abstract SmartRefreshLayout setRefreshLayout();

    protected abstract BaseQuickAdapter setRefreshAdapter();

    /**
     * 触发刷新或加载更多监听
     */
    private void setSmartRefreshLayoutListener() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mAdapter.removeAllFooterView();
                mPageIndex++;
                getData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                getData();
            }
        });
    }

    /**
     * 请求获取数据
     */
    protected abstract void getData();

    /**
     * 刷新或加载更多成功
     */
    protected void refreshOrLoadMoreSuccess(List<T> data) {
        if (mRefreshLayout == null) {
            return;
        }
        mData = data;
        if (mPageIndex == 1) {
            mAdapter.setNewData(data);
            mRefreshLayout.finishRefresh();
        } else {
            if (data != null) {
                mAdapter.addData(data);
            }
            mRefreshLayout.finishLoadMore();
        }
        //数据为空或者回来的数据小于20，则没有更多数据了
        if (data == null || data.size() < 20) {
            mRefreshLayout.setNoMoreData(true);
        }
    }

    /**
     * 请求成功，但是服务器code没返回0000
     */
    protected void refreshOrLoadMoreFail() {
        if (mRefreshLayout == null) {
            return;
        }
        if (mPageIndex == 1) {
            mRefreshLayout.finishRefresh(false);
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }

    /**
     * 请求失败
     */
    protected void refreshOrLoadMoreError() {
        if (mRefreshLayout == null) {
            return;
        }
        //请求失败mPageIndex已经加1，mPageIndex要回退
        if (mPageIndex > 1) {
            mPageIndex--;
            mRefreshLayout.finishLoadMore(false);
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    protected List<T> getListData() {
        return mData;
    }

    /**
     * 设置空布局
     */
    protected void setEmptyView() {
        //空布局
        if (mAdapter.getEmptyViewCount() == 0) {
            mAdapter.setEmptyView(getEmptyView());
        }
    }

    private View getEmptyView() {
        return View.inflate(mActivity, R.layout.l_wallet_empty1, null);
    }
}
