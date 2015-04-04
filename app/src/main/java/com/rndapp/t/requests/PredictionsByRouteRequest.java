package com.rndapp.t.requests;

/**
 * Created by ell on 2/7/15.
 */
public class PredictionsByRouteRequest extends RouteRequest{
    private static final String ENDPOINT = "predictionsbyroute";

    public void get(String route) {
        String url = String.format(URL, ENDPOINT, route);
        super.get(url);
    }
}
