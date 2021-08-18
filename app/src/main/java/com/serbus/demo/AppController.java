package com.serbus.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serbus.amxsdk.TopicListener;
import com.serbus.amxsdk.TrackSDK;

public class AppController extends Application implements TopicListener,Application.ActivityLifecycleCallbacks {
    @Override
    public void onCreate() {
        super.onCreate();
        TrackSDK.initSDK(getApplicationContext(),"ABCD","appID","appKey",this);
    }

    @Override
    public void onTopicReceived(String topic) {
        Log.e("TOPIC",topic);
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        TrackSDK.initSDK(getApplicationContext(),"ABCD","appID","appKey",this);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
