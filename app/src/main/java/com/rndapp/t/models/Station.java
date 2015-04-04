package com.rndapp.t.models;

import android.util.Log;

import com.rndapp.t.models.mbta.MBTAStop;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class Station {
    protected String mStationId;
    protected String mStationName;
    protected ArrayList<MBTAStop> mStops = new ArrayList<>();
    protected ArrayList<Prediction> mPredictions = new ArrayList<>();
    protected ArrayList<Route> mRoutes = new ArrayList<>();

    public Station(String stationId, String stationName) {
        this.mStationId = stationId;
        this.mStationName = stationName;
    }

    public boolean isStopAtStation(MBTAStop stop){
        boolean result = mStops.contains(stop);
        if (stop.getParentId() != null) result = result || stop.getParentId().equals(mStationId);
        return result;
    }

    public void addStop(MBTAStop stop){
        if (mStops.size() == 0 || isStopAtStation(stop)) {
            mStops.add(stop);

            if (mStationId == null && mStationName == null){
                mStationId = stop.getParentId();
                mStationName = stop.getParentName();
            }
        }
    }

    public void addPrediction(Prediction prediction){
        mPredictions.add(prediction);
        if (!mRoutes.contains(prediction.getRoute())){
            mRoutes.add(prediction.getRoute());
        }
    }

    public ArrayList<Prediction> getPredictionsForRoute(Route route){
        ArrayList<Prediction> predictions = new ArrayList<>();
        for (Prediction prediction : mPredictions){
            if (prediction.getRoute().equals(route)){
                predictions.add(prediction);
            }
        }
        return predictions;
    }

    public void clearPredictions(){
        mPredictions = new ArrayList<>();
    }

    public String getStationName() {
        String stationName = mStationName;
        if (stationName != null
                && stationName.contains("Station")
                && !stationName.contains("South")
                && !stationName.contains("North")){
            stationName = stationName.substring(0, stationName.indexOf("Station"));
        }
        return stationName;
    }

    public ArrayList<Prediction> getPredictions() {
        return mPredictions;
    }

    public ArrayList<Route> getRoutes() {
        return mRoutes;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) return false;

        Station otherStation = (Station)o;

        for (MBTAStop stop : otherStation.mStops){
            if (this.mStops.contains(stop)) return true;
        }

        return otherStation.mStationId.equals(this.mStationId);
    }
}
