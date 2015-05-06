package com.rndapp.t.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.rndapp.t.R;

/**
 * Created by ell on 1/18/15.
 */
public class LineFragment extends Fragment {
    protected RecyclerView.Adapter adapter;

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line, container, false);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.line_list);
        recyclerView.setHasFixedSize(true);
        if (adapter != null) recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        return rootView;
    }
}
