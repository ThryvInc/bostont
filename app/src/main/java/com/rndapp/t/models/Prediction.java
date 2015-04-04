package com.rndapp.t.models;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ell on 2/8/15.
 */
public class Prediction implements Comparable<Prediction>{
    protected int mPredictionInSeconds;
    protected Route mRoute;
    protected String mDirectionName;

    public Prediction(int predictionInSeconds, Route route, String directionName) {
        this.mPredictionInSeconds = predictionInSeconds;
        this.mRoute = route;
        this.mDirectionName = directionName;
    }

    public int getPredictionInSeconds() {
        return mPredictionInSeconds;
    }

    public Route getRoute() {
        return mRoute;
    }

    public String getDirectionName() {
        return mDirectionName;
    }

    public static Prediction earliestPrediction(ArrayList<Prediction> predictions){
        if (predictions.size() < 1) return null;
        Collections.sort(predictions);
        return predictions.get(0);
    }

    public static Prediction earliestOtherDirectionPrediction(ArrayList<Prediction> predictions, Prediction firstPrediction){
        if (predictions.size() < 1) return null;
        Collections.sort(predictions);
        for (Prediction prediction : predictions){
            if (!prediction.getDirectionName().equals(firstPrediction.getDirectionName())) return prediction;
        }
        return null;
    }

    @Override
    public int compareTo(Prediction another) {
        int result;
        if (this.getPredictionInSeconds() < 0) result = another.getPredictionInSeconds() - this.getPredictionInSeconds();
        else result = this.getPredictionInSeconds() - another.getPredictionInSeconds();
        return result;
    }
}
