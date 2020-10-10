package com.example.baselibrary.dagger;

import com.example.baselibrary.config.AppApi;
import com.example.baselibrary.http.RetrofitEngine;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    AppApi providerApi(){
        return RetrofitEngine.getInstance().create(AppApi.class);
    }
}
