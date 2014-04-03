package com.rndapp.t.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.rndapp.t.R;
import com.rndapp.t.activities.BostonTActivity;
import com.rndapp.t.adapters.ScheduleAdapter;
import com.rndapp.t.models.Stop;

import org.json.JSONObject;

/**
 * Displays the list of stops for a specific line. Created by kmchen1 on 4/2/14.
 */
public class StopsFragment extends ListFragment {

    /**
     * The managing {@link com.rndapp.t.activities.BostonTActivity} callback.
     */
    OnStopSelectedListener mCallback;

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
         * @param stop The selected {@code Stop}.
         */
        public void onStopSelected(final Stop stop);

        /**
         * TODO is it appropriate to put this in a listener interface? Not sure... Retrieves JSON
         * schedule from the managing callback activity.
         *
         * @return JSON schedule retrieved from the managing callback activity.
         */
        public JSONObject getFetchedData();
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
     * Updates the underlying adapter given a JSONObject. The managing callback activity calls this
     * method.
     *
     * @param fetchedData The JSONObject used to create the underlying ScheduleAdapter.
     */
    public void updateListAdapter(JSONObject fetchedData) {
        setListAdapter(new ScheduleAdapter(getActivity(), R.layout.item, fetchedData));
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

}
