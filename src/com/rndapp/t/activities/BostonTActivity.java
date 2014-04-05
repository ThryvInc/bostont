package com.rndapp.t.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private static final String JSON_URL_PATH = "http://developer.mbta.com/lib/rthr/";

    /**
     * The API key for this project.
     */
    private static final String API_KEY = "G9S4S9H9JXBK884NW625";

    /**
     * Used for debug printing with Log.d
     */
    private static final String TAG = "BostonTActivity";

    /**
     * Queue of Volley requests.
     */
    private RequestQueue mRequestQueue;

    /**
     * Shows a "Loading..." message when user clicks a subway line button.
     */
    private ProgressDialog mProgressDialog;

    /**
     * A modifiable set of name/value mappings. Holds the schedule String.
     */
    private JSONObject mFetchedData;

    /**
     * The last line color fetched. See {@link Trip#BLUE}, {@link Trip#GREEN}, etc. This can also be
     * used as a tag when dealing with fragment transactions.
     */
    private String mlastLineColorFetched;

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
            FragmentTransaction ft = getFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment);
            ft.addToBackStack(null);
            ft.commit();

        }
    }

    /**
     * Fetches data from the web and stores it in {@link com.rndapp.t.activities.BostonTActivity#mFetchedData}.
     * Called when a subway line is pressed.
     *
     * @param lineColor The line to fetch data for. See {@link Trip#ORANGE}, e.g.
     */
    private void fetchData(final String lineColor) {
        mlastLineColorFetched = lineColor;
        mProgressDialog = ProgressDialog.show(this, "", "Loading the " + lineColor + " line...", true, true);
        mRequestQueue.add(createRequestToJsonSchedule(lineColor));
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
     * Creates a Volley JSON request object to a given line color.
     *
     * @param lineColor The line color of the subway, e.g., {@link Trip#GREEN}.
     * @return a Volley JSON request object to a given line color.
     */
    private JsonObjectRequest createRequestToJsonSchedule(final String lineColor) {
        return new JsonObjectRequest(Request.Method.GET, getURL(lineColor),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject fetchedData) {
                        BostonTActivity.this.mFetchedData = fetchedData;

                        // Get current fragment,
                        Fragment f = getFragmentManager().findFragmentByTag(mlastLineColorFetched);
                        if (f != null) {
                            Log.d(TAG, "Found fragment for: " + mlastLineColorFetched);
                        }
                        ((StopsFragment) f).updateListAdapter(fetchedData);

                        mProgressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("VolleyError: " + volleyError.getMessage());
                        BostonTActivity.this.mFetchedData = null;
                        Toast.makeText(BostonTActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
        );
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
     * Checks whether we have an internet connection.
     *
     * @return True if we are connected.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    ////////////////// IMPLEMENTATIONS OF FRAGMENT CALLBACK INTERFACES //////////////////
    /////////////////////////////////////////////////////////////////////////////////////

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
        final Fragment linesFragment = new LinesFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, linesFragment, LinesFragment.class.getName());
        //transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
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

        if (lineColor.equals(Trip.GREEN)) {
            Toast.makeText(this, "Green Line will be supported soon", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Fetching data for " + lineColor + " line...");
        fetchData(lineColor);

        if (mFetchedData == null && !mlastLineColorFetched.equals(lineColor)) {
            Log.d(TAG, "Fetched Data == null && lastColorFetched == " + mlastLineColorFetched);
            return;
        }

        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // TODO how do we get the fragment? what if it exists?
        boolean found = fragmentManager.findFragmentById(R.id.fragment_stops) != null;
        Log.d(TAG, "FragmentManager found StopsFragment?: " + found);

        // R.id.fragment_stops is in layout-large. if it's non-null, we're in dual pane mode
        // StopsFragment newFragment = (StopsFragment) getFragmentManager().findFragmentById(R.id.fragment_stops);

        final Fragment newFragment = StopsFragment.newInstance(lineColor);
        final String newFragmentTag = lineColor;
        //transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
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
        final Fragment mapFragment = new MapFragment();
        //transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
        transaction.replace(R.id.fragment_container, mapFragment, MapFragment.class.getName()).commit();
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