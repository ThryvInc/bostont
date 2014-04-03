package com.rndapp.t.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.rndapp.t.R;
import com.rndapp.t.activities.BostonTActivity;
import com.rndapp.t.adapters.ScheduleAdapter;

import org.json.JSONObject;

/**
 * Displays the list of stops for a specific line.
 * Created by kmchen1 on 4/2/14.
 */
public class StopsFragment extends ListFragment {

    private BostonTActivity mCallback;

    /**
     * Lifecycle step 1
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (BostonTActivity) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lifecycle step 4
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Populate list with an array of stops
        final JSONObject fetchedData = mCallback.getFetchedData();
        setListAdapter(new ScheduleAdapter(getActivity(), R.layout.item, fetchedData));
    }

}
