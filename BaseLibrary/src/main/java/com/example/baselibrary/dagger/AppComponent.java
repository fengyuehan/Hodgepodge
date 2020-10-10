package com.example.baselibrary.dagger;

import com.example.baselibrary.config.AppApi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    AppApi getAppApi();
}
