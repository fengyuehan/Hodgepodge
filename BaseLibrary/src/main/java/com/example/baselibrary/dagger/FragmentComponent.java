package com.example.baselibrary.dagger;

import dagger.Component;


@FragmentScoped
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

}
