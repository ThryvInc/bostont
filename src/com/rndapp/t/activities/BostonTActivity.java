package com.rndapp.t.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.rndapp.t.R;
import com.rndapp.t.fragments.LinesFragment;
import com.rndapp.t.fragments.MapFragment;
import com.rndapp.t.fragments.StopsFragment;
import com.rndapp.t.models.Stop;
import com.rndapp.t.models.Trip;

import org.json.JSONObject;

/**
 * Controls fragments. Fetches JSON schedule data from Boston T site. Subclasses ActionBarActivity,
 * which is a FragmentActivity.
 */
public class BostonTActivity extends ActionBarActivity implements
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
    private RequestQueue mRequestQueue;

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
     * Used for debug printing with Log.d
     */
    private static final String TAG = "BostonTActivity";

    /**
     * Lifecycle Step 1.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Initializing the Volley RequestQueue...");
        mRequestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, "Setting contentView...");
        setContentView(R.layout.main);

        // Ensure the activity's layout has the 'fragment_container' FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // We're being restored from a previous state...
            if (savedInstanceState != null) {
                // Return, lest we see overlapping fragments...
                return;
            }

            Log.d(TAG, "Creating MapFragment...");
            MapFragment mapFragment = new MapFragment();

            Log.d(TAG, "Adding the fragment to fragment_container...");
            getFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment).commit();

        }
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
     * Called from {@link com.rndapp.t.fragments.MapFragment}. User wants to view the schedules in
     * button form.
     */
    @Override
    public void showSchedules() {
        final Fragment fragment = new LinesFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
        transaction.commit();
    }

    /**
     * Allows the {@link com.rndapp.t.fragments.LinesFragment} to communicate with this {@code
     * Activity}.
     *
     * @param lineColor The line the user selected, e.g., {@link com.rndapp.t.models.Trip#ORANGE}.
     */
    @Override
    public void onLineSelected(final String lineColor) {

        if (lineColor == Trip.GREEN) {
            Toast.makeText(this, "Green Line will be supported soon", Toast.LENGTH_LONG).show();
            return;
        }

        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // TODO how do we get the fragment? what if it exists?
        // R.id.fragment_stops is in layout-large. if it's non-null, we're in dual pane mode
        // StopsFragment newFragment = (StopsFragment) getFragmentManager().findFragmentById(R.id.fragment_stops);

        final Fragment newFragment = StopsFragment.newInstance(lineColor);

        // TODO Used for FragmentManager.BackStackEntry APIs
        final String newFragmentTag = lineColor;

        fetchData(Trip.BLUE);
        transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, newFragment, newFragmentTag).commit();
    }

    /**
     * Called when the {@link com.rndapp.t.fragments.LinesFragment} asks its callback (i.e., this
     * activity) to switch to the {@link com.rndapp.t.fragments.MapFragment}.
     */
    @Override
    public void showMap() {
        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        final Fragment newFragment = new MapFragment();
        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
        transaction.replace(R.id.fragment_container, newFragment).commit();
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