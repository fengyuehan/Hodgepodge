package com.example.baselibrary.base.mvc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.baselibrary.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public abstract class BaseRefreshActivity<T> extends BaseActivity {
    protected int mPageIndex = 1;
    protected String mSize = "20";
    protected SmartRefreshLayout mRefreshLayout;
    private BaseQuickAdapter mAdapter;
    private List<T> mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSmartRefreshLayout();
    }

    private void initSmartRefreshLayout() {
        mRefreshLayout = setRefreshLayout();
        if (mRefreshLayout == null) {
            throw new IllegalStateException("please set refreshLayout!");
        }
        mAdapter = setRefreshAdapter();
        if (mAdapter == null) {
            throw new IllegalStateException("please set refreshAdapter!");
        }
        setSmartRefreshLayoutListener();
    }

    protected abstract BaseQuickAdapter setRefreshAdapter();

    protected abstract SmartRefreshLayout setRefreshLayout();

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
        mData = data;
        if (mPageIndex == 1) {
            if (data == null) {
                mRefreshLayout.finishRefresh();
            } else {
                mAdapter.addData(data);
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
                mAdapter.addData(data);
                if (data.size() < Integer.parseInt(mSize)) {
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    mRefreshLayout.finishLoadMore();
                }
            }
        }
    }

    /**
     * 请求成功，但是服务器code没返回0000
     */
    protected void refreshOrLoadMoreFail() {
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
        //请求失败mPageIndex已经加1，mPageIndex要回退
        if (mPageIndex > 1) {
            mPageIndex--;
            mRefreshLayout.finishLoadMore(false);
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    public BaseQuickAdapter getAdapter() {
        return mAdapter;
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

    /**
     * 设置更多foot提示
     */
    protected void setNoMoreFootView() {
        if (mAdapter.getFooterLayoutCount() == 0 && mAdapter.getData().size() < 21) {
            mAdapter.addFooterView(getNoMoreFootView(getString(R.string.tips_no_more_desc)));
        }
    }


    protected void setMoreFootView() {
        if (mData == null) {
            return;
        }
        if (mAdapter.getFooterLayoutCount() == 0 && mData.size() == 20) {
            mAdapter.addFooterView(getNoMoreFootView(getString(R.string.tips_more_desc)));
        } else {
            mAdapter.removeAllFooterView();
        }
    }

    public View getNoMoreFootView(String desc) {
        View noMoreView = View.inflate(this, R.layout.l_footer_desc, null);
        ((TextView) noMoreView.findViewById(R.id.tv_desc)).setText(desc);
        return noMoreView;
    }

    private View getEmptyView() {
        return View.inflate(this, R.layout.l_wallet_empty1, null);
    }
}
