package com.rndapp.t.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rndapp.t.R;
import com.rndapp.t.models.ScheduleAdapter;

/**
 * Created by ell on 1/18/15.
 */
public class LineFragment extends Fragment {
    public ScheduleAdapter adapter;

    public void setAdapter(ScheduleAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line, container, false);
        if (adapter != null) ((ListView)rootView.findViewById(R.id.line_list)).setAdapter(adapter);
        return rootView;
    }
}
