package com.example.baselibrary.base.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.baselibrary.util.TUtil;

public class AbsViewModel<T extends AbsRepository> extends AndroidViewModel {
    protected T mRepository;

    public AbsViewModel(@NonNull Application application) {
        super(application);
        mRepository = TUtil.getInstance(this,0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mRepository != null){
            mRepository.unDisposable();
        }
    }
}
