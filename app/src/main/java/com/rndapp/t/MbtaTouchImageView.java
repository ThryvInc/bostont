package com.rndapp.t;

import android.content.Context;
import android.util.AttributeSet;

import com.rndapp.subway_lib.TouchImageView;

/**
 * Created by ell on 1/18/15.
 */
public class MbtaTouchImageView extends TouchImageView {
    public MbtaTouchImageView(Context context) {super(context);}

    public MbtaTouchImageView(Context context, AttributeSet attrs) {super(context, attrs);}

    @Override
    protected float getDoubleTapScaleFactor() {
        return 3.f;
    }
}
