package com.rndapp.t.models.mbta;

import com.google.gson.annotations.SerializedName;
import com.rndapp.t.models.Prediction;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by adam on 1/10/15.
 */
public class MBTAStop {
    @SerializedName("stop_id")
    protected String mStopId;
    @SerializedName("stop_name")
    protected String mStopName;
    @SerializedName("parent_station")
    protected String mParentId;
    @SerializedName("parent_station_name")
    protected String mParentName;
    @SerializedName("stop_lat")
    protected String mStopLatitude;
    @SerializedName("stop_lon")
    protected String mStopLongitude;
    @SerializedName("distance")
    protected String mDistance;
    @SerializedName("pre_away")
    protected String mPrediction;
    @SerializedName("sch_dep_dt")
    protected String mDepartureTime;
    @SerializedName("mode")
    private ArrayList<MBTAMode> mModes;

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) return false;

        MBTAStop otherStop = (MBTAStop)o;

        return otherStop.mStopId.equals(this.mStopId);
    }

    public ArrayList<MBTAMode> getModes() {
        return mModes;
    }

    public String getStopId() {
        return mStopId;
    }

    public void setStopId(String stopId) {
        this.mStopId = stopId;
    }

    public String getStopName() {
        return mStopName;
    }

    public void setStopName(String stopName) {
        this.mStopName = stopName;
    }

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        this.mParentId = parentId;
    }

    public String getParentName() {
        return mParentName;
    }

    public void setParentName(String parentName) {
        this.mParentName = parentName;
    }

    public String getStopLatitude() {
        return mStopLatitude;
    }

    public void setStopLatitude(String stopLatitude) {
        this.mStopLatitude = stopLatitude;
    }

    public String getStopLongitude() {
        return mStopLongitude;
    }

    public void setStopLongitude(String stopLongitude) {
        this.mStopLongitude = stopLongitude;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
    }

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

    public void setPrediction(String prediction) {
        this.mPrediction = prediction;
    }
}
