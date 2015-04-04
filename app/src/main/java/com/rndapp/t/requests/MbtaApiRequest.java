package com.rndapp.t.requests;

import com.rndapp.t.Constants;

/**
 * Created by ell on 1/27/15.
 */
public class MbtaApiRequest {
    private static final String SCHEME = "http://";
    private static final String MBTA_HOST = "realtime.mbta.com/";
    private static final String MBTA_API_PATH = "developer/api/v2/";
    public static final String MBTA_API_KEY_PAIR = "api_key="+ Constants.MBTA_API_KEY;
    public static final String MBTA_API_URL = SCHEME + MBTA_HOST + MBTA_API_PATH;
    public static final String MBTA_FORMAT = "format=json";

}
