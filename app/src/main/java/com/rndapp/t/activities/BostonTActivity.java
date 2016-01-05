package com.rndapp.t.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.xone.XoneManager;
import com.xone.XoneTipStyle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BostonTActivity extends AppCompatActivity implements LineController{
    protected static final String PREFS_NAME = "main_activity";
    protected AdView adView;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Analytics.activityCreated(this);

        XoneManager.init(this, "007f1544b91341dea0c36176dbecd139");
        XoneManager.enableAutoTips(this, XoneTipStyle.newBuilder().setShowFromBottom(true).build());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        adView = (AdView)findViewById(R.id.ad_bottom);
        findViewById(R.id.ad_top).setVisibility(View.GONE);
        setAdOnTop(false);

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://teschrock.wordpress.com/transit-app-privacy-policy/")));
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    XoneManager.locationPermissionGranted();
                } else {
                    XoneManager.locationPermissionDenied();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected boolean isAdOnTop(){
        return false;
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