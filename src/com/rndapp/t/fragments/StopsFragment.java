package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.rndapp.t.R;
import com.rndapp.t.adapters.ScheduleAdapter;
import com.rndapp.t.models.Stop;

import org.json.JSONObject;

/**
 * Displays the list of stops for a specific line. Created by kmchen1 on 4/2/14.
 */
public class StopsFragment extends Fragment {

    /**
     * Used as a key in the {@code Bundle} arguments that get attached to instances of {@code
     * StopsFragment}.
     */
    private static final String LINE_COLOR = "lineColor";

    /**
     * The managing {@link com.rndapp.t.activities.BostonTActivity} callback.
     */
    OnStopSelectedListener mCallback;

    /**
     * The subway line color of the {@code Stop}s being shown. Discovered via {@code Bundle} args.
     */
    String mLineColor;

    /**
     * The adapter that controls this fragment's list of {@code Stop}s.
     */
    ScheduleAdapter mScheduleAdapter;

    /**
     * When pressed, will ask callback activity to re-fetch data.
     */
    // TODO refresh button here? is it even a button? or in menu?
    Button mButtonRefresh;

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
        final JSONObject fetchedData = mCallback.getFetchedData();
        updateListAdapter(fetchedData);
    }

    /**
     * Updates the underlying adapter given a JSONObject. The managing callback activity calls this
     * method.
     *
     * @param fetchedData The JSONObject used to create the underlying ScheduleAdapter.
     */
    public void updateListAdapter(JSONObject fetchedData) {
        mScheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.item_stop, fetchedData);
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
