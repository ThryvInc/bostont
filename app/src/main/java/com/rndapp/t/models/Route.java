package com.rndapp.t.models;

import com.rndapp.t.R;

/**
 * Created by ell on 2/8/15.
 */
public class Route {
    public static final Route ASHMONT = new Route("Red", "Ashmont", "ASHMONT", R.color.red, true);
    public static final Route BRAINTREE = new Route("Red", "Braintree", "BRAINTREE", R.color.red, true);
    public static final Route ORANGE = new Route("Orange", "Orange", "ORANGE", R.color.orange, true);
    public static final Route BLUE = new Route("Blue", "Blue", "BLUE LINE", R.color.blue, true);
    public static final Route B_LINE = new Route("Green-B", "Boston College", "B", R.color.green, false);
    public static final Route C_LINE = new Route("Green-C", "Cleveland", "C", R.color.green, false);
    public static final Route D_LINE = new Route("Green-D", "Riverside", "D", R.color.green, false);
    public static final Route E_LINE = new Route("Green-E", "Heath", "E", R.color.green, false);
    public static final Route[] ALL_ROUTES = {ASHMONT, BRAINTREE, BLUE, B_LINE, C_LINE, D_LINE, E_LINE, ORANGE};

    protected String mRouteId;
    protected String mMbtaRouteId;
    protected String mRouteName;
    protected int mColorId;
    protected boolean isPredictable;

    public Route(String mbtaRouteId, String routeId, String routeName, int colorId, boolean isPredictable) {
        this.mMbtaRouteId = mbtaRouteId;
        this.mRouteId = routeId;
        this.mRouteName = routeName;
        this.mColorId = colorId;
        this.isPredictable = isPredictable;
    }

    public String getMbtaRouteId() {
        return mMbtaRouteId;
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

    public boolean isPredictable() {
        return isPredictable;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) return false;

        Route otherRoute = (Route)o;

        return this.getRouteName().equals(otherRoute.getRouteName());
    }
}
