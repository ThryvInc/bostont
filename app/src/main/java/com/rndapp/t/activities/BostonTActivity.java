package com.rndapp.t.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rndapp.t.fragments.LineFragment;
import com.rndapp.t.fragments.MapFragment;
import com.rndapp.t.fragments.SchedulesFragment;
import com.rndapp.t.models.Analytics;
import com.rndapp.t.models.Line;
import com.rndapp.t.models.LineController;
import com.rndapp.t.R;
import com.rndapp.t.models.Nagger;
import com.rndapp.t.models.StationAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BostonTActivity extends ActionBarActivity implements LineController{
    protected static final String PREFS_NAME = "main_activity";
    protected AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Analytics.activityCreated(this);

        if (isAdOnTop()){
            adView = (AdView)findViewById(R.id.ad_top);
            findViewById(R.id.ad_bottom).setVisibility(View.GONE);
            setAdOnTop(true);
        }else {
            adView = (AdView)findViewById(R.id.ad_bottom);
            findViewById(R.id.ad_top).setVisibility(View.GONE);
            setAdOnTop(false);
        }

        if (!getResources().getString(R.string.version).equals("paid")){
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("124C0C8E23FB2264186BB5819F6A0D57")
                    .build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("onTop", isAdOnTop() ? "true" : "false");
                    FlurryAgent.logEvent("AdClicked", map);
                }
            });
        }else{
            adView.setVisibility(View.GONE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.fl_fragments, new MapFragment(), "Map")
                .commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Analytics.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Analytics.onStop(this);
    }

    @Override
    public void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) adView.resume();

        if (!getResources().getString(R.string.version).equals("paid")) {
            new Nagger(this).startNag();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
    }

    protected boolean isAdOnTop(){
        return this.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
                .getBoolean("adOnTop", new Date().getTime() % 2 == 0);
    }

    protected void setAdOnTop(boolean canNag){
        SharedPreferences.Editor editor = this.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean("adOnTop", canNag).apply();
    }

    @Override
    public void showSchedules() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out)
                .replace(R.id.fl_fragments, new SchedulesFragment(), "Schedules")
                .addToBackStack("Schedules")
                .commitAllowingStateLoss();

        Analytics.schedulesShown(this);
    }

    @Override
    public void showLine(Line line) {
        LineFragment fragment = new LineFragment();
        fragment.setAdapter(new StationAdapter(this, R.layout.station_item, line));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out)
                .replace(R.id.fl_fragments, fragment, "Line")
                .addToBackStack("Line")
                .commitAllowingStateLoss();
    }
}