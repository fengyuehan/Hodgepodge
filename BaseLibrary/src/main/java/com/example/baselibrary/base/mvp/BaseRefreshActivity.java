package com.example.baselibrary.base.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.baselibrary.R;
import com.example.baselibrary.base.mvc.BaseActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import javax.inject.Inject;

public abstract class BaseRefreshActivity<T,V extends IBaseRefreshView,P extends BaseRefreshPresenter<V>> extends BaseActivity implements IBaseRefreshView {
    @Inject
    private P mPresenter;
    protected int mPageIndex = 1;
    protected String mSize = "20";
    protected SmartRefreshLayout mRefreshLayout;
    private BaseQuickAdapter mRefreshAdapter;
    private List<T> mData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        mPresenter.attachView((V) this);
        initRefresh();
    }

    private void initRefresh() {
        mRefreshLayout = setRefreshLayout();
        mRefreshAdapter = setRefreshAdapter();
        setSmartRefreshLayoutListener();
    }

    protected void setSmartRefreshLayoutListener(){
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mRefreshAdapter.removeAllFooterView();
                mPageIndex++;
                mPresenter.getRefreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                mPresenter.getRefreshData();
            }
        });
    }

    protected abstract BaseQuickAdapter setRefreshAdapter();

    protected abstract SmartRefreshLayout setRefreshLayout();

    protected abstract void inject();

    @Override
    public void refreshOrLoadMoreSuccess(List data) {
        mData = data;
        if (mPageIndex == 1) {
            if (data == null) {
                mRefreshLayout.finishRefresh();
            } else {
                mRefreshAdapter.addData(data);
                if (data.size() < Integer.parseInt(mSize)) {
                    mRefreshLayout.finishRefreshWithNoMoreData();
                } else {
                    mRefreshLayout.finishRefresh();
                }
            }
        } else {
            if (data == null) {
                mRefreshLayout.finishLoadMore();
            } else {
                mRefreshAdapter.addData(data);
                if (data.size() < Integer.parseInt(mSize)) {
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    mRefreshLayout.finishLoadMore();
                }
            }
        }
    }

    @Override
    public void refreshOrLoadMoreError() {
        //请求失败mPageIndex已经加1，mPageIndex要回退
        if (mPageIndex > 1) {
            mPageIndex--;
            mRefreshLayout.finishLoadMore(false);
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void refreshOrLoadMoreFail() {
        if (mPageIndex == 1) {
            mRefreshLayout.finishRefresh(false);
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    /**
     * 设置空布局
     */
    protected void setEmptyView() {
        //空布局
        if (mRefreshAdapter.getEmptyViewCount() == 0) {
            mRefreshAdapter.setEmptyView(getEmptyView());
        }
    }

    private View getEmptyView() {
        return View.inflate(this, R.layout.l_wallet_empty1, null);
    }

    protected List<T> getListData() {
        return mData;
    }
}
