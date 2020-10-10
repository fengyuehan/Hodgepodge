package com.example.baselibrary.base.mvp;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.baselibrary.LibApplication;
import com.example.baselibrary.R;
import com.example.baselibrary.base.mvc.BaseFragment;
import com.example.baselibrary.dagger.DaggerFragmentComponent;
import com.example.baselibrary.dagger.FragmentComponent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import javax.inject.Inject;



public abstract class BaseRefreshFragment<T, V extends IBaseRefreshView, P extends BaseRefreshPresenter<V>> extends BaseFragment implements IBaseRefreshView {
    @Inject
    protected P mPresenter;
    protected int mPageIndex = 1;
    protected int mSize = 20;
    protected SmartRefreshLayout mRefreshLayout;
    private BaseQuickAdapter mRefreshAdapter;
    private List<T> mData;
    protected FragmentComponent mFragmentComponent;

    @Override
    protected void init() {
        mFragmentComponent = DaggerFragmentComponent.builder().appComponent(LibApplication.getAppComponent()).build();
        inject();
        mPresenter.attachView(getUiView());
        initRefresh();
    }


    protected abstract void inject();


    protected void initRefresh() {
        mRefreshLayout = setRefreshLayout();
        if (mRefreshLayout == null) {
            throw new IllegalStateException("please set refreshLayout!");
        }
        mRefreshAdapter = setRefreshAdapter();
        if (mRefreshAdapter == null) {
            throw new IllegalStateException("please set refreshAdapter!");
        }
        setSmartRefreshLayoutListener();
    }

    protected abstract SmartRefreshLayout setRefreshLayout();

    protected abstract BaseQuickAdapter setRefreshAdapter();

    private V getUiView() {
        return (V) this;
    }

    /**
     * 触发刷新或加载更多监听
     */
    private void setSmartRefreshLayoutListener() {
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

    /**
     * 刷新或加载更多成功
     */
    @Override
    public void refreshOrLoadMoreSuccess(List data) {
        mData = data;
        if (mPageIndex == 1) {
            mRefreshAdapter.setNewData(data);
            mRefreshLayout.finishRefresh();
        } else {
            if (data != null) {
                mRefreshAdapter.addData(data);
            }
            mRefreshLayout.finishLoadMore();
        }
        //数据为空或者回来的数据小于最大允许回来数据，则没有更多数据了
        if (data == null || data.size() < mSize) {
            mRefreshLayout.setNoMoreData(true);
        }
    }

    /**
     * 请求成功，但是服务器code没返回0000
     */
    @Override
    public void refreshOrLoadMoreFail() {
        if (mPageIndex == 1) {
            mRefreshLayout.finishRefresh(false);
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }

    /**
     * 请求失败
     */
    public void refreshOrLoadMoreError() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        return View.inflate(mActivity, R.layout.l_wallet_empty1, null);
    }

}
