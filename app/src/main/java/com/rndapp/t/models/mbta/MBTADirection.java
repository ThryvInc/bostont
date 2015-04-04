package com.rndapp.t.models.mbta;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class MBTADirection {
    @SerializedName("direction_id")
    protected String mDirectionId;
    @SerializedName("direction_name")
    protected String mDirectionName;
    @SerializedName("stop")
    protected ArrayList<MBTAStop> mStops;
    @SerializedName("trip")
    protected ArrayList<MBTATrip> mTrips;

    public ArrayList<MBTAStop> getStops() {
        return mStops;
    }

    public ArrayList<MBTATrip> getTrips() {
        return mTrips;
    }

    public String getDirectionId() {
        return mDirectionId;
    }

    public String getDirectionName() {
        return mDirectionName;
    }
}
