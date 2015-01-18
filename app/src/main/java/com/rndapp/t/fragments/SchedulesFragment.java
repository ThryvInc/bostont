package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.rndapp.t.R;
import com.rndapp.t.models.Line;
import com.rndapp.t.models.LineController;
import com.rndapp.t.models.LineScheduleLoader;

import org.json.JSONObject;

/**
 * Created by ell on 1/18/15.
 */
public class SchedulesFragment extends Fragment implements View.OnClickListener, LineScheduleLoader.OnLineScheduleLoadedListener{

    // appears when request is loading schedule
    protected ProgressDialog pd;

    public enum BostonLine implements Line {
        ORANGE("orange"),
        RED("red"),
        BLUE("blue");

        // cruft in html: e.g., http://developer.mbta.com/lib/rthr/blue.json
        private final String name;

        private BostonLine(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedules, container, false);
        rootView.findViewById(R.id.blue_btn).setOnClickListener(this);
        rootView.findViewById(R.id.green_btn).setOnClickListener(this);
        rootView.findViewById(R.id.orange_btn).setOnClickListener(this);
        rootView.findViewById(R.id.red_btn).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof LineController)){

        }
    }


    @Override
    public void onLineScheduleLoaded(JSONObject jsonObject) {
        pd.dismiss();

        // just in case, check for success, then toggle animations
        if (jsonObject != null) {
            ((LineController)getActivity()).showLine(jsonObject);
        }
    }

    @Override
    public void onFailure(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.orange_btn:
                makeRequest(BostonLine.ORANGE);
                break;

            case R.id.red_btn:
                makeRequest(BostonLine.RED);
                break;

            case R.id.blue_btn:
                makeRequest(BostonLine.BLUE);
                break;

            // TODO - alternative... there's no json schedule for green line
            case R.id.green_btn:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Green Line");
                builder.setMessage("Unfortunately, the MBTA does not offer schedules for the Green line. But as soon as they do, we'll make sure it gets to you!");
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
                break;
        }
    }

    protected final void makeRequest(Line line) {
        pd = ProgressDialog.show(getActivity(), "", "Loading", true, true);
        LineScheduleLoader.load(this, getLineUrl(line));
    }

    protected final String getLineUrl(Line line) {
        return "http://developer.mbta.com/lib/rthr/" + line.getName() + ".json";
    }
}
