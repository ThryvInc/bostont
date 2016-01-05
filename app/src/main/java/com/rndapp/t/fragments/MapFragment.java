package com.rndapp.t.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rndapp.subway_lib.TouchImageView;
import com.rndapp.t.R;
import com.rndapp.t.models.Analytics;
import com.rndapp.t.models.LineController;

/**
 * Created by ell on 1/18/15.
 */
public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        root.findViewById(R.id.see_sched).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LineController)getActivity()).showSchedules();
            }
        });

        root.findViewById(R.id.tv_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://teschrock.wordpress.com/transit-app-privacy-policy/")));
            }
        });

        TouchImageView img = (TouchImageView)root.findViewById(R.id.touchImg);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap subway = BitmapFactory.decodeResource(getResources(), R.drawable.subway, options);
        img.setImageBitmap(subway);

        Analytics.mapShown(getActivity());

        return root;
    }
}
