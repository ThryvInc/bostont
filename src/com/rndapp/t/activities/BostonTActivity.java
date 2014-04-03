package com.rndapp.t.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.rndapp.t.fragments.LinesFragment;
import com.rndapp.t.fragments.MapFragment;
import com.rndapp.t.fragments.StopsFragment;
import com.rndapp.t.models.Stop;
import com.rndapp.t.models.Trip;

import org.json.JSONObject;

/**
 * A subclass of MainActivity in the subway_lib submodule. Responsible for fetching JSON files from
 * the Boston T website.
 */
public class BostonTActivity extends MainActivity
        implements OnClickListener,
        MapFragment.OnMapLineSelectedListener,
        LinesFragment.OnLineSelectedListener,
        StopsFragment.OnStopSelectedListener {

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
     * A modifiable set of name/value mappings. Holds the schedule String.
     */
    private JSONObject mFetchedData;

    /**
     * Shows a "Loading..." message when user clicks a subway line button.
     */
    private ProgressDialog mProgressDialog;

    /**
     * The last line color fetched. See {@link Trip#BLUE}, {@link Trip#GREEN}, etc. This can also be
     * used as a tag when dealing with fragment transactions.
     */
    private String mlastLineColorFetched;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Check that the activity is using the layout version with the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // The initial fragment - MapFragment - to be placed in the activity layout
            MapFragment mapFragment = new MapFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mapFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment).commit();

        }
    }

    /**
     * Manages all clicks on Views. This is where Fragment transactions occur.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);

        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment newFragment = null;
        // TODO Used for FragmentManager.BackStackEntry APIs
        String newFragmentTag = null;

        switch (v.getId()) {

            /* The See Schedules button is shown above the ImageView of the BostonT. */
            case R.id.btn_see_schedules:
                // shows the schedule (i.e., the stops for each line)
                newFragment = new LinesFragment();
                transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
                transaction.addToBackStack(null);
                break;

            /* The See Map button is shown above the colorful buttons of each subway line. */
            case R.id.btn_see_map:
                newFragment = new MapFragment();
                transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
                break;

            /*
             * Pressing a subway line button fetches schedule data and
             * passes it to the fragment responsible for showing stops.
             */
            case R.id.btn_orange:
                newFragment = StopsFragment.newInstance(Trip.ORANGE);
                newFragmentTag = Trip.ORANGE;
                fetchData(Trip.ORANGE);
                transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
                transaction.addToBackStack(null);
                break;
            case R.id.btn_red:
                newFragment = StopsFragment.newInstance(Trip.RED);
                newFragmentTag = Trip.RED;
                fetchData(Trip.RED);
                transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
                transaction.addToBackStack(null);
                break;
            case R.id.btn_blue:
                newFragment = StopsFragment.newInstance(Trip.BLUE);
                newFragmentTag = Trip.BLUE;
                fetchData(Trip.BLUE);
                transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
                transaction.addToBackStack(null);
                break;

            // TODO PLUG IN GREEN LINE
            case R.id.btn_green:
                // this just displays Green Line unavailability notification
                startActivity(new Intent(this, Notification.class));
                break;
            default:
                Toast.makeText(this, "What other View was clicked?", Toast.LENGTH_LONG).show();
                break;
        }
        transaction.replace(R.id.fragment_container, newFragment, newFragmentTag).commit();
    }

    /**
     * Fetches data from the web and stores it in a field. Called when a subway line is pressed.
     * This method calls {@link com.rndapp.t.activities.BostonTActivity#getSchedule(String)}.
     *
     * @param lineColor The line to fetch data for. See {@link Trip#ORANGE}, e.g.
     */
    private void fetchData(final String lineColor) {
        mlastLineColorFetched = lineColor;
        mProgressDialog = ProgressDialog.show(this, "", "Loading...", true, true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mFetchedData = null;
                getSchedule(lineColor);
            }
        });
        thread.start();
    }

    /**
     * Returns the URL of a specific subway line.
     *
     * @param line The color of the subway line to retrieve (e.g., {@link Trip#ORANGE}, or
     *             "orange").
     * @return the URL of the given line's JSON schedule. If line is "orange", then this method
     * returns "http://developer.mbta.com/lib/rthr/orange.json".
     */
    public String getURL(final String line) {
        return JSON_URL_PATH + line + ".json";
    }

    /**
     * Volley makes a GET request for JSON file and then saves the fetched JSON object.
     *
     * @param line The color of the subway line to retrieve. See {@link Trip#ORANGE}, e.g.
     */
    private void getSchedule(final String line) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getURL(line),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject fetchedData) {
                        BostonTActivity.this.mFetchedData = fetchedData;
                        mProgressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.getMessage());
                        BostonTActivity.this.mFetchedData = null;
                        Toast.makeText(BostonTActivity.this, "VolleyError", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
        );
        mRequestQueue.add(request);
    }

    /**
     * When this Activity starts, it also starts or continues a Flurry session for the project
     * denoted by the API key.
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

    /**
     * Allows the {@link com.rndapp.t.fragments.MapFragment} to communicate with this {@code
     * Activity}.
     *
     * @param lineColor The line the user selected, e.g., {@link com.rndapp.t.models.Trip#ORANGE}.
     */
    @Override
    public void onMapLineSelected(final String lineColor) {
        // TODO for future, when we have a subway-line-highlightable imageview
    }

    /**
     * Allows the {@link com.rndapp.t.fragments.LinesFragment} to communicate with this {@code
     * Activity}.
     *
     * @param lineColor The line the user selected, e.g., {@link com.rndapp.t.models.Trip#ORANGE}.
     */
    @Override
    public void onLineSelected(final String lineColor) {
        // TODO what happens when user presses a color-coded subway button
        StopsFragment stopsFragment = (StopsFragment) getFragmentManager().findFragmentById(R.id.fragment_stops);
    }

    /**
     * Allows the {@link com.rndapp.t.fragments.StopsFragment} to communicate with this {@code
     * Activity}.
     *
     * @param stop The selected {@code Stop}.
     */
    @Override
    public void onStopSelected(final Stop stop) {
        // TODO for future, when we want to create another stop-info fragment (re: tourists)
    }

    /**
     * Returns the {@code JSONObject} schedule that was last fetched. Used by {@link
     * com.rndapp.t.fragments.StopsFragment}.
     *
     * @return The {@code JSONObject} schedule that was fetched for a given subway line.
     */
    @Override
    public JSONObject getFetchedData() {
        return mFetchedData;
    }

    /**
     * Used by {@link com.rndapp.t.fragments.StopsFragment} to re-fetch data.
     *
     * @param lineColor Passed by fragment so this activity knows what data to fetch.
     */
    @Override
    public void refresh(final String lineColor) {
        // Updates the instance variable with newly fetched data
        fetchData(mlastLineColorFetched);
        // Update the fragment with this data
        final Fragment fragment = getFragmentManager().findFragmentByTag(mlastLineColorFetched);
        ((StopsFragment) fragment).updateListAdapter(mFetchedData);

    }

}