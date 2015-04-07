package com.rndapp.t.models.mbta;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class MBTATrip {
    @SerializedName("trip_headsign")
    protected String mHeadsign;
    @SerializedName("trip_name")
    protected String mName;
    @SerializedName("stop")
    protected ArrayList<MBTAStop> mStops;

    public String getName() {
        return mName;
    }

    public String getHeadsign() {
        return mHeadsign;
    }

    public ArrayList<MBTAStop> getStops() {
        return mStops;
    }
}
