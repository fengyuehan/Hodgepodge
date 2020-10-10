package com.example.baselibrary.base.mvp;

public interface IBaseView {
    /**
     * 显示加载框
     */
    void showLoading();


    /**
     * 关闭加载框
     */
    void closeLoading();

    /**
     * 网络加载错误
     *
     * @param msg 错误信息
     */
    void onNetError(String msg);

    /**
     * 网络加载业务上失败
     *
     * @param msg 失败信息
     */
    void onNetFailed(String msg);
}
