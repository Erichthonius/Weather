package com.example.lenovo.weather;

import android.app.Application;

import com.thinkland.sdk.android.JuheSDKInitializer;

/**
 * Created by lenovo on 2016/5/13 0013.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JuheSDKInitializer.initialize(getApplicationContext());
    }
}
