package com.rndapp.t.requests;

/**
 * Created by ell on 4/4/15.
 */
public class SchedulesByRouteRequest extends RouteRequest{
    private static final String ENDPOINT = "schedulebyroute";

    public void get(String route) {
        String url = String.format(URL, ENDPOINT, route);
        super.get(url);
    }
}
