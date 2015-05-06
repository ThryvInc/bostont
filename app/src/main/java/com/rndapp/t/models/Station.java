package com.rndapp.t.models;

import android.util.Log;

import com.android.volley.VolleyError;
import com.rndapp.t.models.mbta.MBTADirection;
import com.rndapp.t.models.mbta.MBTAMode;
import com.rndapp.t.models.mbta.MBTARoute;
import com.rndapp.t.models.mbta.MBTAStop;
import com.rndapp.t.models.mbta.MBTATrip;
import com.rndapp.t.requests.PredictionByStopRequest;
import com.rndapp.t.requests.ScheduleByStopRequest;
import com.rndapp.t.requests.StopRequest;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class Station implements StopRequest.StopRequestCallback{
    protected String mStationId;
    protected String mStationName;
    protected ArrayList<MBTAStop> mStops = new ArrayList<>();
    protected ArrayList<Prediction> mPredictions = new ArrayList<>();
    protected ArrayList<Route> mRoutes = new ArrayList<>();
    protected OnStationRefreshCallback mCallback;
    protected int semaphore = 0;

    public interface OnStationRefreshCallback{
        public void onStationRefreshSuccess();
        public void onStationRefreshFailure();
    }

    public Station(String stationId, String stationName) {
        this.mStationId = stationId;
        this.mStationName = stationName;
    }

    public void refresh(final OnStationRefreshCallback callback){
        mCallback = callback;
        boolean hasScheduledRoute = false;
        boolean hasPredictedRoute = false;
        for (Route route : mRoutes){
            if (!route.isPredictable()) hasScheduledRoute = true;
            if (route.isPredictable()) hasPredictedRoute = true;
        }

        if (hasPredictedRoute){
            PredictionByStopRequest request = new PredictionByStopRequest();
            request.setCallback(this);
            request.get(mStationId);
            semaphore++;
        }

        if (hasScheduledRoute){
            ScheduleByStopRequest scheduleRequest = new ScheduleByStopRequest();
            scheduleRequest.setCallback(this);
            scheduleRequest.get(mStationId);
            semaphore++;
        }
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

    @Override
    public void onStopSuccess(MBTAStop stop, final boolean isFromPrediction) {
        //remove all predictions
        mPredictions = new ArrayList<>();
        //for all modes at stop
        if (stop != null){
            for (MBTAMode mode : stop.getModes()){
                //if mode < 2
                if (Integer.parseInt(mode.getRouteType()) < 2){
                    //for all mbta routes
                    for (MBTARoute mbtaRoute : mode.getRoutes()){
                        //for all directions
                        for (MBTADirection mbtaDirection : mbtaRoute.getDirections()){
                            //for all trips
                            for (MBTATrip trip : mbtaDirection.getTrips()){
                                //for all routes
                                for (Route route : Route.ALL_ROUTES){
                                    //if trip name contains route name
                                    if ((trip.getName().toLowerCase() + " ").contains(" " + route.getRouteId().toLowerCase() + " ") && route.isPredictable() == isFromPrediction){
                                        int predictionInSeconds = Integer.parseInt(trip.getPrediction());
                                        if (predictionInSeconds > -1){
                                            addPrediction(new Prediction(predictionInSeconds, route, mbtaDirection.getDirectionName()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        semaphore--;
        if (semaphore == 0 && mCallback != null) mCallback.onStationRefreshSuccess();
    }

    @Override
    public void onStopFailure(VolleyError error) {
        semaphore--;
        if (semaphore == 0 && mCallback != null) mCallback.onStationRefreshFailure();
    }
}
