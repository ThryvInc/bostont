package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rndapp.t.R;
import com.rndapp.t.models.Analytics;
import com.rndapp.t.models.Line;
import com.rndapp.t.models.LineController;
import com.rndapp.t.models.Route;

/**
 * Created by ell on 1/18/15.
 */
public class SchedulesFragment extends Fragment implements View.OnClickListener, Line.OnLoadingCompleteCallback{

    // appears when request is loading schedule
    protected ProgressDialog pd;

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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.orange_btn:
                Analytics.orangeLinePressed(getActivity());
                Route[] orangeRoutes = {new Route("903_", "Oak Grove", R.color.orange), new Route("913_", "WELLINGTON", R.color.orange)};
                getLineWithRoutes(orangeRoutes, true);
                break;

            case R.id.red_btn:
                Analytics.redLinePressed(getActivity());
                Route[] redRoutes = {new Route("931_", "ASHMONT", R.color.red), new Route("933_", "BRAINTREE", R.color.red)};
                getLineWithRoutes(redRoutes, true);
                break;

            case R.id.blue_btn:
                Analytics.blueLinePressed(getActivity());
                Route[] blueRoutes = {new Route("946_", "BLUE LINE", R.color.blue)};
                getLineWithRoutes(blueRoutes, true);
                break;

            case R.id.green_btn:
                Analytics.greenLinePressed(getActivity());
                Route[] greenRoutes = {new Route("Green-B", "B", R.color.green),
                        new Route("Green-C", "C", R.color.green),
                        new Route("Green-D", "D", R.color.green),
                        new Route("Green-E", "E", R.color.green)};
                getLineWithRoutes(greenRoutes, false);
                break;
        }
    }

    protected final void getLineWithRoutes(Route[] routes, boolean shouldPredict){
        pd = ProgressDialog.show(getActivity(), "", "Loading", true, true);
        Line line = new Line(routes, shouldPredict, this);
    }

    @Override
    public void onLineLoadedSuccess(Line line) {
        pd.dismiss();
        if (getActivity() != null) ((LineController)getActivity()).showLine(line);
    }

    @Override
    public void onLineLoadedFailure() {
        pd.dismiss();
        if (getActivity() != null)
            Toast.makeText(getActivity(), "Check your internet connection and try again", Toast.LENGTH_LONG).show();
    }
}
