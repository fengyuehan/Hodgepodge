package com.example.baselibrary.base.mvvm;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.baselibrary.base.mvc.BaseFragment;
import com.example.baselibrary.event.LiveBus;
import com.example.baselibrary.stateview.ErrorState;
import com.example.baselibrary.stateview.LoadingState;
import com.example.baselibrary.stateview.StateConstants;
import com.example.baselibrary.util.TUtil;
import com.tqzhang.stateview.core.LoadManager;
import com.tqzhang.stateview.stateview.BaseStateControl;
import com.tqzhang.stateview.stateview.SuccessState;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsLifecycleFragment<T extends AbsViewModel> extends BaseFragment {
    protected T mViewModel;
    protected LoadManager loadManager;
    private List<Object> eventKeys = new ArrayList<>();

    @Override
    protected void init() {
        mViewModel = new ViewModelProvider(this).get((Class<T>) TUtil.getInstance(this, 0));
        if (mViewModel != null){
            mViewModel.mRepository.mState.observe(this,observer);
        }
        loadManager = new LoadManager.Builder()
                .setViewParams(this)
                .setListener(new BaseStateControl.OnRefreshListener() {
                    @Override
                    public void onRefresh(View view) {
                        showLoading();
                    }
                }).build();
    }

    protected void showLoading() {
        loadManager.showStateView(LoadingState.class);
    }

    protected void showSuccess(){
        loadManager.showStateView(SuccessState.class);
    }

    protected void showError(){
        loadManager.showStateView(ErrorState.class);
    }

    protected <T> MutableLiveData<T> registerSubscriber(Object eventKey, Class<T> tClass){
        return registerSubscriber(eventKey,null,tClass);
    }

    private <T> MutableLiveData<T> registerSubscriber(Object eventKey, String tag, Class<T> tClass) {
        String event ;
        if (TextUtils.isEmpty(tag)){
            event = (String) eventKey;
        }else {
            event = eventKey + tag;
        }
        eventKeys.add(event);
        return LiveBus.getInstance().subscribe(eventKey,tag,tClass);
    }

    private Observer observer = new Observer<String>() {
        @Override
        public void onChanged(String state) {
            if (!TextUtils.isEmpty(state)){
                if (StateConstants.ERROR_STATE.equals(state) || StateConstants.NET_WORK_STATE.equals(state)){
                    showError();
                }else if (StateConstants.LOADING_STATE.equals(state)){
                    showLoading();
                }else if (StateConstants.SUCCESS_STATE.equals(state)){
                    showSuccess();
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearEvent();
    }

    private void clearEvent() {
        if (eventKeys != null && eventKeys.size() > 0) {
            for (int i = 0; i < eventKeys.size(); i++) {
                LiveBus.getInstance().clear(eventKeys.get(i));
            }
        }
    }
}
