package com.rndapp.t.requests;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.rndapp.t.MbtaApplication;
import com.rndapp.t.models.mbta.MBTAStop;

import org.json.JSONObject;

/**
 * Created by ell on 4/13/15.
 */
public class StopRequest extends MbtaApiRequest{
    protected static final String KEY_PAIRS = "stop=%s";
    protected static final String ENDPOINT_AND_KEY_PAIR = "%s?" + KEY_PAIRS;
    protected static final String URL = MBTA_API_URL + ENDPOINT_AND_KEY_PAIR + "&" + MBTA_FORMAT + "&" + MBTA_API_KEY_PAIR;

    protected StopRequestCallback mCallback;

    public void setCallback(StopRequestCallback callback) {
        this.mCallback = callback;
    }

    public interface StopRequestCallback{
        public void onStopSuccess(MBTAStop stop, boolean isFromPrediction);
        public void onStopFailure(VolleyError error);
    }

    public void get(String url, final boolean isFromPrediction){
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MBTAStop stop = new Gson().fromJson(response.toString(), MBTAStop.class);
                if (mCallback != null) mCallback.onStopSuccess(stop, isFromPrediction);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mCallback != null) mCallback.onStopFailure(error);
            }
        });
        MbtaApplication.getInstance().getRequestQueue().add(request);
    }
}
