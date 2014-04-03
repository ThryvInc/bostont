package com.rndapp.t.fragments;

import android.app.Activity;
import android.app.Fragment;

import com.rndapp.subway_lib.TouchImageView;

/**
 * Displays the subway line map. Created by kmchen1 on 4/2/14.
 */
public class MapFragment extends Fragment {

    /**
     * The {@link com.rndapp.t.activities.BostonTActivity} that implements {@link
     * com.rndapp.t.fragments.MapFragment.OnMapLineSelectedListener}.
     */
    OnMapLineSelectedListener mCallback;

    /**
     * The {@link com.rndapp.subway_lib.TouchImageView} that allows users to interact with the map
     * and select subway lines. Once a line is highlighted, user may press See Schedules button
     * to view the stops, which is shown by the {@link com.rndapp.t.fragments.StopsFragment}.
     */
    TouchImageView subwayMap;

    /**
     * TODO this is for the future. User can highlight/select a color-coded subway line on the
     * interactive image, which then tells the activity to load schedules for that line. This could
     * potentially eliminate the need for the middle {@link com.rndapp.t.fragments.LinesFragment},
     * which originally displayed the color-coded buttons for all the subway lines.
     */
    public interface OnMapLineSelectedListener {
        /**
         * The user highlights a subway line on the interactive map, and then presses See Schedules,
         * which takes user directly to the {@link com.rndapp.t.fragments.StopsFragment}, which
         * shows a list of all the {@link com.rndapp.t.models.Trip}s and {@link
         * com.rndapp.t.models.Stop}s along that line.
         *
         * @param lineColor The line the user selected, e.g., {@link com.rndapp.t.models.Trip#ORANGE}.
         */
        public void onMapLineSelected(String lineColor);
    }

    /**
     * Lifecycle Step 1.
     *
     * @param activity The {@code Activity} to which this {@code Fragment} attaches.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Ensure the container activity has implemented the callback interface.
        try {
            mCallback = (OnMapLineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnMapLineSelectedListener.class.getName());
        }
    }

}
