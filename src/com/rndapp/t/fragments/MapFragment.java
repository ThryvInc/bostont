package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rndapp.t.TouchImageView;
import com.rndapp.t.R;

/**
 * Displays the subway line map. Created by kmchen1 on 4/2/14.
 */
public class MapFragment extends Fragment implements View.OnClickListener {

    /**
     * The {@link com.rndapp.t.activities.BostonTActivity} that implements {@link
     * com.rndapp.t.fragments.MapFragment.OnMapLineSelectedListener}.
     */
    private OnMapLineSelectedListener mCallback;

    /**
     * The {@link com.rndapp.subway_lib.TouchImageView} that allows users to interact with the map
     * and select subway lines. Once a line is highlighted, user may press See Schedules button to
     * view the stops, which is shown by the {@link com.rndapp.t.fragments.StopsFragment}.
     */
    private TouchImageView mSubwayMap;

    /**
     * If a line is highlighted, takes user to {@link com.rndapp.t.fragments.StopsFragment}. If no
     * subway line is highlighted, takes user to {@link com.rndapp.t.fragments.LinesFragment}.
     */
    private Button mSeeSchedulesButton;

    /**
     * {@link com.rndapp.t.activities.BostonTActivity} will implement this interface.
     */
    public interface OnMapLineSelectedListener {
        /**
         * Called when the user has highlighted a subway line on {@link
         * com.rndapp.t.fragments.MapFragment#mSubwayMap} and has pressed {@link
         * com.rndapp.t.fragments.MapFragment#mSeeSchedulesButton}. The managing callback should
         * then take the user directly to the {@link com.rndapp.t.fragments.StopsFragment}, which
         * shows a list of all the {@link com.rndapp.t.models.Trip}s and {@link
         * com.rndapp.t.models.Stop}s along selected subway line.
         *
         * @param lineColor The line the user selected, e.g., {@link com.rndapp.t.models.Trip#ORANGE}.
         */
        public void onMapLineSelected(String lineColor);

        /**
         * Tells callback activity to switch to {@link com.rndapp.t.fragments.LinesFragment}.
         */
        public void showSchedules();
    }

    /**
     * Called when user clicks a button.
     *
     * @param v The button that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_see_schedules:
                mCallback.showSchedules();
                break;
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
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Called when a menu item has been selected.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_see_schedules:
                mCallback.showSchedules();
                break;
            case R.id.menu_item_help:
                Toast.makeText((Activity) mCallback, "MapFrag - TODO", Toast.LENGTH_SHORT).show();
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
            mCallback = (OnMapLineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnMapLineSelectedListener.class.getName());
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
        final View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Set the image
        mSubwayMap = (TouchImageView) v.findViewById(R.id.touchImg);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap subway = BitmapFactory.decodeResource(getResources(), R.drawable.subway, options);
        mSubwayMap.setImageBitmap(subway);

        mSeeSchedulesButton = (Button) v.findViewById(R.id.btn_see_schedules);
        mSeeSchedulesButton.setOnClickListener(this);
        return v;
    }

}
