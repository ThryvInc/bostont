package com.rndapp.t.models.mbta;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

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
    @SerializedName("sch_dep_dt")
    protected String mDepartureTime;
    @SerializedName("pre_away")
    protected String mPrediction;

    public String getPrediction() {
        if (mDepartureTime != null && mPrediction == null){
            Date now = new Date();
            Date departureTime = new Date(Long.valueOf(mDepartureTime) * 1000);

            long predictionInMilliseconds = departureTime.getTime() - now.getTime();
            long predictionInSeconds = predictionInMilliseconds / 1000;

            mPrediction = String.valueOf(predictionInSeconds);
        }
        return mPrediction;
    }

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
