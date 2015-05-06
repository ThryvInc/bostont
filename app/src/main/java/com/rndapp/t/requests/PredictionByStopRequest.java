package com.rndapp.t.requests;

/**
 * Created by ell on 4/9/15.
 */
public class PredictionByStopRequest extends StopRequest{
    private static final String ENDPOINT = "predictionsbystop";

    public void get(String station){
        super.get(String.format(URL, ENDPOINT, station), true);
    }

}
