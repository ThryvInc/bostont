package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rndapp.t.R;
import com.rndapp.t.adapters.ScheduleAdapter;
import com.rndapp.t.models.Stop;

import org.json.JSONObject;

/**
 * Displays the list of stops for a specific line. Created by kmchen1 on 4/2/14.
 */
public class StopsFragment extends ListFragment {

    /**
     * Used as a key in the {@code Bundle} arguments that get attached to instances of {@code
     * StopsFragment}.
     */
    private static final String LINE_COLOR = "lineColor";

    /**
     * The managing {@link com.rndapp.t.activities.BostonTActivity} callback.
     */
    private OnStopSelectedListener mCallback;

    /**
     * The subway line color of the {@code Stop}s being shown. Discovered via {@code Bundle} args.
     */
    private String mLineColor;

    /**
     * The adapter that controls this fragment's list of {@code Stop}s.
     */
    private ScheduleAdapter mScheduleAdapter;

    /**
     * When pressed, will ask callback activity to re-fetch data.
     */
    // TODO refresh button here? is it even a button? or in menu?
    private Button mButtonRefresh;

    /**
     * An interface for allowing this fragment to communicate with its callback activity. When a
     * {@link com.rndapp.t.models.Stop} is selected, information is sent back to the managing
     * callback activity. Such information may or may not be useful in the future (e.g., if we
     * wanted to add another fragment displaying info about the stop.) Re: tourists.
     */
    public interface OnStopSelectedListener {
        /**
         * The {@link com.rndapp.t.activities.BostonTActivity} will implement this method, which is
         * called when the user selects a {@code Stop}.
         *
         * @param stop This fragment passes the callback the selected {@code Stop}.
         */
        public void onStopSelected(final Stop stop);

        /**
         * Retrieves JSON schedule from the managing callback activity.
         *
         * @return JSON schedule retrieved from the managing callback activity.
         */
        public JSONObject getFetchedData();

        /**
         * Forces a call to {@link com.rndapp.t.activities.BostonTActivity#fetchData(String)}, and
         * then calls {@link com.rndapp.t.activities.BostonTActivity#getFetchedData()}, and then
         * uses that data to call notifyDataSetChanged somewhere.
         *
         * @param lineColor This fragment passes the callback the subway line color, so it knows
         *                  what data to retrieve.
         */
        public void refresh(String lineColor);
    }

    /**
     * Creates this fragment's menu.
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stops, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Called when a menu item has been selected.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_refresh:
                mCallback.refresh(mLineColor);
                break;
            case R.id.menu_item_help:
                Toast.makeText((Activity) mCallback, "StopsFrag - TODO", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * Lifecycle step 1.
     *
     * @param activity The activity to which this fragment attaches.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnStopSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnStopSelectedListener.class.getName());
        }
    }

    /**
     * Lifecycle Step 2.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLineColor = getArguments().getString(LINE_COLOR);

    }

    /**
     * Lifecycle Step 3.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final View v = inflater.inflate(R.layout.fragment_stops, container, false);
        //final ListView lv = (ListView) v.findViewById(R.id.line_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Lifecycle step 4. Populate list with an array of stops
     *
     * @param savedInstanceState Will be null if the activity is being created for the first time.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateListAdapter(mCallback.getFetchedData());
    }

    /**
     * Updates the underlying adapter given a JSONObject. Called by callback activity.
     *
     * @param fetchedData The JSONObject used to create the underlying ScheduleAdapter.
     */
    public void updateListAdapter(final JSONObject fetchedData) {
        if (fetchedData != null) {
            mScheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.item_stop, fetchedData);
            setListAdapter(mScheduleAdapter);
            mScheduleAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Creates a fragment that displays {@code Stop}s for a given line color.
     *
     * @param lineColor The given line color.
     * @return a fragment that displays {@code Stop}s for a given line color.
     */
    public static StopsFragment newInstance(final String lineColor) {
        StopsFragment stopsFragment = new StopsFragment();
        Bundle args = new Bundle();
        args.putString(LINE_COLOR, lineColor);
        stopsFragment.setArguments(args);
        return stopsFragment;
    }


}
