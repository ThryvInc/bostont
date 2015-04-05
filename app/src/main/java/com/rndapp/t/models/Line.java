package com.rndapp.t.models;

import android.util.Log;

import com.android.volley.VolleyError;
import com.rndapp.t.models.mbta.MBTADirection;
import com.rndapp.t.models.mbta.MBTARoute;
import com.rndapp.t.models.mbta.MBTAStop;
import com.rndapp.t.models.mbta.MBTATrip;
import com.rndapp.t.requests.PredictionsByRouteRequest;
import com.rndapp.t.requests.RouteRequest;
import com.rndapp.t.requests.SchedulesByRouteRequest;
import com.rndapp.t.requests.StopsByRouteRequest;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class Line {
    protected Route[] mRoutes;
    protected ArrayList<Station> mStations = new ArrayList<>();
    protected OnLoadingCompleteCallback mCallback;
    protected int mRequestSemaphore = 0;
    protected int mErrorCount = 0;

    public interface OnLoadingCompleteCallback {
        public void onLineLoadedSuccess(Line line);
        public void onLineLoadedFailure(boolean usersFault);
    }

    public Line(Route[] routes, boolean shouldGetPredictions, OnLoadingCompleteCallback callback) {
        this.mRoutes = routes;
        this.mCallback = callback;

        getRoutes(shouldGetPredictions);
    }

    protected void getRoutes(boolean shouldGetPredictions){
        mErrorCount = 0;
        for (Route route : mRoutes){
            getStopsForRoute(route, shouldGetPredictions);
        }
    }

    protected void getStopsForRoute(final Route route, final boolean shouldGetPredictions){
        StopsByRouteRequest request = new StopsByRouteRequest();
        request.setCallback(new RouteRequest.RouteRequestCallback() {
            @Override
            public void onRouteSuccess(MBTARoute mbtaRoute) {
                if (mbtaRoute != null && mbtaRoute.getDirections() != null){
                    for (MBTADirection direction : mbtaRoute.getDirections()){
                        if (direction.getStops() != null){
                            addStopsToStations(route, direction.getStops(), direction.getDirectionName());
                        }
                    }
                }
                if (shouldGetPredictions){
                    getPredictionsForRoute(route);
                }else {
                    getSchedulesForRoute(route);
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                if (mCallback != null) mCallback.onLineLoadedFailure(error.networkResponse == null || error.networkResponse.statusCode < 300);
            }
        });
        request.get(route.getRouteId());
    }

    protected void getPredictionsForRoute(final Route route){
        mRequestSemaphore++;
        PredictionsByRouteRequest request = new PredictionsByRouteRequest();
        request.setCallback(new RouteRequest.RouteRequestCallback() {
            @Override
            public void onRouteSuccess(MBTARoute mbtaRoute) {
                if (mbtaRoute != null && mbtaRoute.getDirections() != null){
                    for (MBTADirection direction : mbtaRoute.getDirections()){
                        if (direction.getTrips() != null){
                            for (MBTATrip trip : direction.getTrips()){
                                if (trip.getStops() != null){
                                    addStopsToStations(route, trip.getStops(), direction.getDirectionName());
                                }
                            }
                        }
                    }
                }
                mRequestSemaphore--;
                if (Line.this.mCallback != null && mRequestSemaphore == 0) Line.this.mCallback.onLineLoadedSuccess(Line.this);
            }

            @Override
            public void onFailure(VolleyError error) {
                mRequestSemaphore--;
                mErrorCount++;
                if (Line.this.mCallback != null && mRequestSemaphore == 0 && mErrorCount > 0){
                    Line.this.mCallback.onLineLoadedFailure(error.networkResponse == null || error.networkResponse.statusCode < 300);
                }
            }
        });
        request.get(route.getRouteId());
    }

    protected void getSchedulesForRoute(final Route route){
        mRequestSemaphore++;
        SchedulesByRouteRequest request = new SchedulesByRouteRequest();
        request.setCallback(new RouteRequest.RouteRequestCallback() {
            @Override
            public void onRouteSuccess(MBTARoute mbtaRoute) {
                if (mbtaRoute != null && mbtaRoute.getDirections() != null){
                    for (MBTADirection direction : mbtaRoute.getDirections()){
                        if (direction.getTrips() != null){
                            for (MBTATrip trip : direction.getTrips()){
                                if (trip.getStops() != null){
                                    addStopsToStations(route, trip.getStops(), direction.getDirectionName());
                                }
                            }
                        }
                    }
                }
                mRequestSemaphore--;
                if (Line.this.mCallback != null && mRequestSemaphore == 0) Line.this.mCallback.onLineLoadedSuccess(Line.this);
            }

            @Override
            public void onFailure(VolleyError error) {
                mRequestSemaphore--;
                mErrorCount++;
                if (Line.this.mCallback != null && mRequestSemaphore == 0 && mErrorCount > 0){
                    Line.this.mCallback.onLineLoadedFailure(error.networkResponse == null || error.networkResponse.statusCode < 300);
                }
            }
        });
        request.get(route.getRouteId());
    }

    protected synchronized void addStopsToStations(Route route, ArrayList<MBTAStop> stops, String directionName){
        for (MBTAStop stop : stops){
            Prediction prediction = null;
            if (stop.getPrediction() != null && Integer.parseInt(stop.getPrediction()) > -1){
                prediction = new Prediction(Integer.parseInt(stop.getPrediction()), route, directionName);
            }

            if (stop.getParentId() != null && stop.getParentName() != null){
                Station station = new Station(stop.getParentId(), stop.getParentName());
                station.addStop(stop);
                if (mStations.contains(station)){
                    mStations.get(mStations.indexOf(station)).addStop(stop);
                    if (prediction != null) mStations.get(mStations.indexOf(station)).addPrediction(prediction);
                }else {
                    if (prediction != null) station.addPrediction(prediction);

                    mStations.add(station);
                }
            }else {
                for (Station station : mStations){
                    if (prediction != null && station.isStopAtStation(stop)) station.addPrediction(prediction);
                }
            }
        }
    }
}
