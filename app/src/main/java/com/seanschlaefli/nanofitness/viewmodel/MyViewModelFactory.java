package com.seanschlaefli.nanofitness.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Application mApplication;
    private Object[] mParams;

    public MyViewModelFactory(Application application, Object... objects) {
        mApplication = application;
        mParams = objects;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T result = null;
        if (modelClass == StepRecordViewModel.class) {
            return (T) new StepRecordViewModel(mApplication, (Integer) mParams[0]);
        } else if (modelClass == LocationRecordViewModel.class) {
            return (T) new LocationRecordViewModel(mApplication, (Integer) mParams[0]);
        } else {
            return super.create(modelClass);
        }
    }

}
