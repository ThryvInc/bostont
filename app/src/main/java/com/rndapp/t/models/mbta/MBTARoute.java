package com.rndapp.t.models.mbta;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class MBTARoute {
    @SerializedName("route_id")
    private String mRouteId;
    private String mRouteType;
    private String mRouteName;
    @SerializedName("direction")
    protected ArrayList<MBTADirection> mDirections;

    public String getRouteId() {
        return mRouteId;
    }

    public void setRouteId(String mRouteId) {
        this.mRouteId = mRouteId;
    }

    public void setRouteType(String mRouteType) {
        this.mRouteType = mRouteType;
    }

    public String getRouteName() {
        return mRouteName;
    }

    public ArrayList<MBTADirection> getDirections() {
        return mDirections;
    }
}
