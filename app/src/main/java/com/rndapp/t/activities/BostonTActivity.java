package com.rndapp.t.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

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
import com.rndapp.t.models.StationAdapter;

public class BostonTActivity extends ActionBarActivity implements LineController{
    AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Analytics.activityCreated(this);

        if (!getResources().getString(R.string.version).equals("paid")){
            adView = (AdView)this.findViewById(R.id.ad);
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
            });
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
    }

    @Override
    public void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
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