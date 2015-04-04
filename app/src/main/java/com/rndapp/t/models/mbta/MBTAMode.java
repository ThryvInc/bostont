package com.rndapp.t.models.mbta;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ell on 2/7/15.
 */
public class MBTAMode {
    @SerializedName("route_type")
    private String mRouteType;
    @SerializedName("route")
    private ArrayList<MBTARoute> mRoutes;


}
