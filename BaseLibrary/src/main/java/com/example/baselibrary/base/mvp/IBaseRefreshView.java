package com.example.baselibrary.base.mvp;

import java.util.List;

public interface IBaseRefreshView extends IBaseView {
    void refreshOrLoadMoreSuccess(List data);

    void refreshOrLoadMoreFail();

    void refreshOrLoadMoreError();
}
