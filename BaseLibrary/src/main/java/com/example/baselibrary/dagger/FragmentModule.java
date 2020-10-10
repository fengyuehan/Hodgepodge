package com.example.baselibrary.dagger;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @FragmentScoped
    @Provides
    Fragment provider() {
        return mFragment;
    }
}
