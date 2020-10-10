package com.example.baselibrary.dagger;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private Activity mActivity;

    private ActivityModule(Activity mActivity){
        this.mActivity = mActivity;
    }

    @ActivityScoped
    @Provides
    Activity provider(){
        return mActivity;
    }
}
