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
                Route[] orangeRoutes = {Route.ORANGE};
                getLineWithRoutes(orangeRoutes);
                break;

            case R.id.red_btn:
                Analytics.redLinePressed(getActivity());
                Route[] redRoutes = {Route.ASHMONT, Route.BRAINTREE};
                getLineWithRoutes(redRoutes);
                break;

            case R.id.blue_btn:
                Analytics.blueLinePressed(getActivity());
                Route[] blueRoutes = {Route.BLUE};
                getLineWithRoutes(blueRoutes);
                break;

            case R.id.green_btn:
                Analytics.greenLinePressed(getActivity());
                Route[] greenRoutes = {Route.B_LINE,
                        Route.C_LINE,
                        Route.D_LINE,
                        Route.E_LINE};
                getLineWithRoutes(greenRoutes);
                break;
        }
    }

    protected final void getLineWithRoutes(Route[] routes){
        pd = ProgressDialog.show(getActivity(), "", "Loading", true, true);
        Line line = new Line(routes, this);
    }

    @Override
    public void onLineLoadedSuccess(Line line) {
        pd.dismiss();
        if (getActivity() != null) ((LineController)getActivity()).showLine(line);
    }

    @Override
    public void onLineLoadedFailure(boolean usersFault) {
        pd.dismiss();
        if (getActivity() != null) {
            String message = "The MBTA is currently not responding";
            if (usersFault) message = "Check your internet connection and try again";
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
