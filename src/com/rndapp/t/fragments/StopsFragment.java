package com.rndapp.t.fragments;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.rndapp.t.R;
import com.rndapp.t.adapters.ScheduleAdapter;
import com.rndapp.t.models.Stop;

/**
 * Displays the list of stops for a specific line.
 * Created by kmchen1 on 4/2/14.
 */
public class StopsFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Populate list with an array of stops

        setListAdapter(new ScheduleAdapter(getActivity(), R.layout.item, fetchedData));
    }

}
