package com.rndapp.t.requests;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.rndapp.t.MbtaApplication;
import com.rndapp.t.models.mbta.MBTARoute;

import org.json.JSONObject;

/**
 * Created by ell on 2/7/15.
 */
public class RouteRequest extends MbtaApiRequest{
    protected static final String KEY_PAIRS = "route=%s";
    protected static final String ENDPOINT_AND_KEY_PAIR = "%s?" + KEY_PAIRS;
    protected static final String URL = MBTA_API_URL + ENDPOINT_AND_KEY_PAIR + "&" + MBTA_FORMAT + "&" + MBTA_API_KEY_PAIR;

    protected RouteRequestCallback mCallback;

    public interface RouteRequestCallback{
        public void onRouteSuccess(MBTARoute route);
        public void onFailure(VolleyError error);
    }

    public void setCallback(RouteRequestCallback callback) {
        this.mCallback = callback;
    }

    protected void get(String url){
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MBTARoute route = new Gson().fromJson(response.toString(), MBTARoute.class);
                if (mCallback != null) mCallback.onRouteSuccess(route);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mCallback != null) mCallback.onFailure(error);
            }
        });
        MbtaApplication.getInstance().getRequestQueue().add(request);
    }
}
