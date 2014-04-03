package com.rndapp.t.fragments;

import android.app.Fragment;

/**
 * Displays the buttons for each subway line color.
 * Created by kmchen1 on 4/2/14.
 */
public class LinesFragment extends Fragment {

    public interface OnLineSelectedListener {
        public void onLineSelected(final String lineColor);
    }

}
