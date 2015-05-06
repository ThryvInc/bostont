package com.rndapp.t.requests;

/**
 * Created by ell on 4/13/15.
 */
public class ScheduleByStopRequest extends StopRequest{
    private static final String ENDPOINT = "schedulebystop";

    public void get(String station){
        super.get(String.format(URL, ENDPOINT, station), false);
    }
}
