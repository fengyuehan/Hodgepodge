package com.example.baselibrary.http;

import com.example.baselibrary.http.convert.ApiException;
import com.example.baselibrary.http.util.ExceptionHandle;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by yf on 2017/11/29.
 * Email：yunfei10306@163.com
 * 描述：联网接口结果回调
 */

public abstract class CallBack<T> implements Observer<T> {

    public abstract void onSuccess(T t);

    public abstract void onFail(int code, String message);


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            String errorCode = ((ApiException) e).getErrorCode();
            /**
             * 在这里可以做一些token失效拦截
             */
            /*if (errorCode.equals(Constants.CODE_NO_LOGIN)) {
                login();
                onFail(NO_LOGIN, AntApplication.getInstance().getString(R.string.tips_not_login1));
                return;
            }*/

        }

        ExceptionHandle.ResponseThrowable responseThrowable = ExceptionHandle.handleException(e);
        onFail(responseThrowable.code, responseThrowable.message);
    }

    @Override
    public void onComplete() {

    }

}
