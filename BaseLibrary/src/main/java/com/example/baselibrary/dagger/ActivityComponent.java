package com.example.baselibrary.dagger;

import dagger.Component;
@ActivityScoped
@Component(dependencies = AppComponent.class,modules = ActivityModule.class)
public interface ActivityComponent {

}
