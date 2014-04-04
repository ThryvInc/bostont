package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rndapp.t.R;
import com.rndapp.t.models.Trip;

/**
 * Displays the buttons for each subway line color. Created by kmchen1 on 4/2/14.
 */
public class LinesFragment extends Fragment implements View.OnClickListener {

    /**
     * Button for the Subway Map.
     */
    Button mButtonSeeMap;

    /**
     * Button for the Green Line.
     */
    Button mButtonGreen;

    /**
     * Button for the Blue Line.
     */
    Button mButtonBlue;

    /**
     * Button for the Orange Line.
     */
    Button mButtonOrange;

    /**
     * Button for the Red Line.
     */
    Button mButtonRed;

    /**
     * The managing callback activity (i.e., {@link com.rndapp.t.activities.BostonTActivity}).
     */
    OnLineSelectedListener mCallback;

    /**
     * {@link com.rndapp.t.activities.BostonTActivity} will implement this interface.
     */
    public interface OnLineSelectedListener {
        /**
         * Called when the user clicks a button corresponding to one of the subway lines.
         *
         * @param lineColor The color of the clicked button/subway line.
         */
        public void onLineSelected(final String lineColor);

        /**
         * Tells callback activity to switch to the {@link com.rndapp.t.fragments.MapFragment}.
         */
        public void showMap();
    }

    /**
     * When user clicks one of the subway line buttons, pass the info to our callback.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_see_map:
                mCallback.showMap();
                break;
            case R.id.btn_blue:
                mCallback.onLineSelected(Trip.BLUE);
                break;
            case R.id.btn_green:
                mCallback.onLineSelected(Trip.GREEN);
                break;
            case R.id.btn_orange:
                mCallback.onLineSelected(Trip.ORANGE);
                break;
            case R.id.btn_red:
                mCallback.onLineSelected(Trip.RED);
                break;
            default:
                Log.e(getClass().getName(), "What other subway lines are there?");
        }
    }

    /**
     * Creates this fragment's menu.
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lines, menu);
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
            case R.id.menu_item_see_map:
                mCallback.showMap();
                break;
            case R.id.menu_item_help:
                Toast.makeText((Activity) mCallback, "LinesFrag - TODO", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    /**
     * Lifecycle Step 1.
     *
     * @param activity The {@code Activity} to which this {@code Fragment} attaches.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnLineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnLineSelectedListener.class.getName());
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
    }

    /**
     * Lifecycle Step 3. Inflate the fragment layout file. Load the buttons.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_lines, container, false);
        mButtonBlue = (Button) v.findViewById(R.id.btn_blue);
        mButtonGreen = (Button) v.findViewById(R.id.btn_green);
        mButtonOrange = (Button) v.findViewById(R.id.btn_orange);
        mButtonRed = (Button) v.findViewById(R.id.btn_red);
        mButtonSeeMap = (Button) v.findViewById(R.id.btn_see_map);

        mButtonBlue.setOnClickListener(this);
        mButtonGreen.setOnClickListener(this);
        mButtonOrange.setOnClickListener(this);
        mButtonRed.setOnClickListener(this);
        mButtonSeeMap.setOnClickListener(this);

        mButtonBlue.setBackgroundColor(getResources().getColor(R.color.blue));
        mButtonGreen.setBackgroundColor(getResources().getColor(R.color.green));
        mButtonOrange.setBackgroundColor(getResources().getColor(R.color.orange));
        mButtonRed.setBackgroundColor(getResources().getColor(R.color.red));

        return v;
    }

    /**
     * Lifecycle Step 4.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
