package com.rndapp.t;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.rndapp.t.models.Analytics;
import com.xone.XoneManager;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ell on 1/18/15.
 */
public class MbtaApplication extends Application {
    private static MbtaApplication mInstance;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(this);

        Fabric.with(this, new Crashlytics());
        Analytics.init(this);
    }

    public static MbtaApplication getInstance(){return mInstance;};

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
