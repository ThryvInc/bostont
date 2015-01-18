package com.rndapp.t.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.flurry.android.FlurryAgent;
import com.rndapp.t.fragments.LineFragment;
import com.rndapp.t.fragments.MapFragment;
import com.rndapp.t.fragments.SchedulesFragment;
import com.rndapp.t.models.LineController;
import com.rndapp.t.R;
import com.rndapp.t.models.ScheduleAdapter;

import org.json.JSONObject;

/**
 * A subclass of MainActivity in the subway_lib submodule.
 */
public class BostonTActivity extends ActionBarActivity implements LineController{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fl_fragments, new MapFragment(), "Map")
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "G9S4S9H9JXBK884NW625");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void showSchedules() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out)
                .replace(R.id.fl_fragments, new SchedulesFragment(), "Schedules")
                .addToBackStack("Schedules")
                .commit();
    }

    @Override
    public void showLine(JSONObject jsonObject) {
        LineFragment fragment = new LineFragment();
        fragment.setAdapter(new ScheduleAdapter(this, R.layout.item, jsonObject));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out)
                .replace(R.id.fl_fragments, fragment, "Line")
                .addToBackStack("Line")
                .commit();
    }
}