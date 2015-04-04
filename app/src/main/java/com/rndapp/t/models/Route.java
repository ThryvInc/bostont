package com.rndapp.t.models;

/**
 * Created by ell on 2/8/15.
 */
public class Route {
    protected String mRouteId;
    protected String mRouteName;
    protected int mColorId;

    public Route(String routeId, String routeName, int colorId) {
        this.mRouteId = routeId;
        this.mRouteName = routeName;
        this.mColorId = colorId;
    }

    public String getRouteId() {
        return mRouteId;
    }

    public String getRouteName() {
        return mRouteName;
    }

    public int getColorId() {
        return mColorId;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) return false;

        Route otherRoute = (Route)o;

        return this.getRouteId().equals(otherRoute.getRouteId());
    }
}
