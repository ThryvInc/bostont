package com.rndapp.t.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.rndapp.subway_lib.MainActivity;
import com.rndapp.subway_lib.Notification;
import com.rndapp.t.R;
import com.rndapp.t.adapters.ScheduleAdapter;
import com.rndapp.t.models.Trip;

import org.json.JSONObject;

/**
 * A subclass of MainActivity in the subway_lib submodule.
 * Responsible for fetching JSON files from the Boston T website.
 */
public class BostonTActivity extends MainActivity implements OnClickListener {

    /**
     * The path to the Boston T schedule JSON files.
     */
    private final String JSON_URL_PATH = "http://developer.mbta.com/lib/rthr/";

    /**
     * Queue of Volley requests.
     */
    private final RequestQueue mRequestQueue = Volley.newRequestQueue(this);

    /**
     * The API key for this project.
     */
    private static final String API_KEY = "G9S4S9H9JXBK884NW625";

    /**
     * A modifiable set of name/value mappings.
     * Holds the schedule String.
     */
    private JSONObject mFetchedData;

    /**
     * Shows a "Loading..." message when user clicks a subway line button.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Allows us to schedule {@code Message}s and {@code Runnable}s
     * to be executed at some point in the future. Enqueued objects
     * will be called by the current thread's {@code MessageQueue}
     * when they are received.
     */
    private Handler mHandler = new Handler() {
        /**
         * Populates the ListView.
         * @param msg The received message (usually just a blank 0).
         */
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            if (mFetchedData != null) {
                final ScheduleAdapter sa = new ScheduleAdapter(context, R.layout.item, mFetchedData);
                final ListView lv = (ListView) findViewById(R.id.line_list);
                lv.setAdapter(sa);
                // TODO send fetchedData to Fragment
            } else {
                Toast.makeText(context, "Please make sure you are connected to the internet.", Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        setXML();
    }

    /**
     * Assigns a background color and {@code OnClickListener} to each of the subway line buttons.
     */
    @Override
    protected void setXML() {
        super.setXML();

        final Button red = (Button) findViewById(R.id.btn_red);
        final Button blue = (Button) findViewById(R.id.btn_blue);
        final Button orange = (Button) findViewById(R.id.btn_orange);
        final Button green = (Button) findViewById(R.id.btn_green);

        red.setOnClickListener(this);
        red.setBackgroundColor(getResources().getColor(R.color.red));
        blue.setOnClickListener(this);
        blue.setBackgroundColor(getResources().getColor(R.color.blue));
        orange.setOnClickListener(this);
        orange.setBackgroundColor(getResources().getColor(R.color.orange));
        green.setOnClickListener(this);
        green.setBackgroundColor(getResources().getColor(R.color.green));
    }

    /**
     * Manages all clicks on Views.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);

        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        Fragment newFragment = null;

        switch (v.getId()) {
            case R.id.see_map:
                newFragment = fm.findFragmentById(R.id.map_fragment);
                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
                break;
            case R.id.see_sched:
                // shows the schedule (i.e., the stops for each line)
                newFragment = fm.findFragmentById(R.id.stops_fragment);
                ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
                ft.addToBackStack(null);
                break;
            case R.id.btn_orange:
                fetchData(Trip.ORANGE);
                break;
            case R.id.btn_red:
                fetchData(Trip.RED);
                break;
            case R.id.btn_blue:
                fetchData(Trip.BLUE);
                break;
            // TODO - alternative... there's no json schedule for green line
            case R.id.btn_green:
                // this just displays Green Line unavailability notification
                startActivity(new Intent(this, Notification.class));
                break;
            default:
                Toast.makeText(this, "What other View was clicked?", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Fetches data from the web. Called when a subway line is pressed.
     *
     * @param lineColor The line to fetch data for.
     */
    private void fetchData(final String lineColor) {
        mProgressDialog = ProgressDialog.show(this, "", "Loading...", true, true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mFetchedData = null;
                getSchedule(lineColor);
                // Invokes mHandler's handleMessage, which creates new ScheduleAdapter
                mHandler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    /**
     * Returns the URL of a specific subway line.
     *
     * @param line The color of the subway line to retrieve (e.g., "orange").
     * @return the URL of the given line's JSON schedule. If line is "orange", then this method
     * returns "http://developer.mbta.com/lib/rthr/orange.json".
     */
    public String getURL(final String line) {
        return JSON_URL_PATH + line + ".json";
    }

    /**
     * Volley makes a GET request for JSON file and then saves the fetched JSON object.
     *
     * @param line The color of the subway line to retrieve (e.g., "orange").
     */
    private void getSchedule(final String line) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getURL(line),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject fetchedData) {
                        BostonTActivity.this.mFetchedData = fetchedData;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.getMessage());
                        BostonTActivity.this.mFetchedData = null;
                    }
                }
        );
        mRequestQueue.add(request);
    }

    /**
     * When this Activity starts, it also starts or continues
     * a Flurry session for the project denoted by the API key.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, API_KEY);
    }

    /**
     * When this Activity stops, it stops the Flurry session.
     */
    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}