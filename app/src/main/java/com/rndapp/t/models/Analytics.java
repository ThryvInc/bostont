package com.rndapp.t.models;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ell on 1/19/15.
 */
public class Analytics {
    private static final String PARSE_KEY_ONE = "X3yDzgLXHAGEUtZNvDgzxXL2duKaxh0dlQCCklde";
    private static final String PARSE_KEY_TWO = "2eSxhEZUTVqcoqetHe33yeG36B8mXjz26fxWkj8n";
    private static final String FLURRY_KEY = "G9S4S9H9JXBK884NW625";
    private static final String GA_PROPERTY_ID = "UA-58717858-1";

    private static final String STARTED_APP_KEY = "StartedApp";
    private static final String SESSION_END_KEY = "SessionEnding";
    private static final String MAP_KEY = "Map";
    private static final String SCHEDULES_KEY = "Schedules";
    private static final String BLUE_LINE_KEY = "BlueLinePressed";
    private static final String GREEN_LINE_KEY = "ImplementedGreenLinePressed";
    private static final String RED_LINE_KEY = "RedLinePressed";
    private static final String ORANGE_LINE_KEY = "OrangeLinePressed";
    private static final String APP_OPENS_KEY = "NumberOfAppOpens";
    private static final String NAG_RATING = "NagRating-v1";
    private static final String NAG_APP = "NagApp-v1";

    private static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    public enum TrackerName {
        APP_TRACKER
    }

    public static void init(Application application){
        Parse.initialize(application, PARSE_KEY_ONE, PARSE_KEY_TWO);
    }

    public static void activityCreated(ActionBarActivity activity){
        ParseAnalytics.trackAppOpenedInBackground(activity.getIntent());
    }

    public static void onStart(Context context){
        incrementNumberOfOpens(context);

        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setAction(STARTED_APP_KEY)
                .set(APP_OPENS_KEY, Integer.toString(getNumberOfOpens(context)))
                .build());

        //Values
        Map<String, String> values = new HashMap<>();
        values.put(APP_OPENS_KEY, Integer.toString(getNumberOfOpens(context)));

        //Parse
        ParseAnalytics.trackEventInBackground(STARTED_APP_KEY, values);

        //Flurry
        FlurryAgent.logEvent(STARTED_APP_KEY, values);

        FlurryAgent.onStartSession(context, FLURRY_KEY);
    }

    public static void onStop(Context context){
        //Values
        Map<String, String> values = new HashMap<>();
        values.put(APP_OPENS_KEY, Integer.toString(getNumberOfOpens(context)));

        ParseAnalytics.trackEventInBackground(SESSION_END_KEY, values);

        FlurryAgent.logEvent(SESSION_END_KEY, values);
        FlurryAgent.onEndSession(context);
    }

    public static void mapShown(Context context){
        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.setScreenName(MAP_KEY);
        t.send(new HitBuilders.AppViewBuilder().build());

        //Parse
        ParseAnalytics.trackEventInBackground(MAP_KEY);

        //Flurry
        FlurryAgent.logEvent(MAP_KEY);
    }

    public static void nagRating(Context context, String response){
        Map<String, String> map = new HashMap<String, String>();
        map.put("numberOfAppOpens", numberGroup(Analytics.getNumberOfOpens(context)));
        map.put("response", response);

        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setAction(NAG_RATING)
                .set("numberOfAppOpens", numberGroup(Analytics.getNumberOfOpens(context)))
                .set("response", response)
                .build());

        //Parse
        ParseAnalytics.trackEventInBackground(NAG_RATING, map);

        //Flurry
        FlurryAgent.logEvent(NAG_RATING, map);
    }

    public static void nagApp(Context context, String response){
        Map<String, String> map = new HashMap<String, String>();
        map.put("numberOfAppOpens", numberGroup(Analytics.getNumberOfOpens(context)));
        map.put("response", response);

        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setAction(NAG_APP)
                .set("numberOfAppOpens", numberGroup(Analytics.getNumberOfOpens(context)))
                .set("response", response)
                .build());

        //Parse
        ParseAnalytics.trackEventInBackground(NAG_APP, map);

        //Flurry
        FlurryAgent.logEvent(NAG_APP, map);
    }

    public static void schedulesShown(Context context){
        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.setScreenName(SCHEDULES_KEY);
        t.send(new HitBuilders.AppViewBuilder().build());

        //Parse
        ParseAnalytics.trackEventInBackground(SCHEDULES_KEY);

        //Flurry
        FlurryAgent.logEvent(SCHEDULES_KEY);
    }

    public static void redLinePressed(Context context){
        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.setScreenName(SCHEDULES_KEY);
        t.send(new HitBuilders.EventBuilder()
                .setAction(RED_LINE_KEY)
                .build());

        //Parse
        ParseAnalytics.trackEventInBackground(RED_LINE_KEY);

        //Flurry
        FlurryAgent.logEvent(RED_LINE_KEY);
    }

    public static void blueLinePressed(Context context){
        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.setScreenName(SCHEDULES_KEY);
        t.send(new HitBuilders.EventBuilder()
                .setAction(BLUE_LINE_KEY)
                .build());

        //Parse
        ParseAnalytics.trackEventInBackground(BLUE_LINE_KEY);

        //Flurry
        FlurryAgent.logEvent(BLUE_LINE_KEY);
    }

    public static void orangeLinePressed(Context context){
        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.setScreenName(SCHEDULES_KEY);
        t.send(new HitBuilders.EventBuilder()
                .setAction(ORANGE_LINE_KEY)
                .build());

        //Parse
        ParseAnalytics.trackEventInBackground(ORANGE_LINE_KEY);

        //Flurry
        FlurryAgent.logEvent(ORANGE_LINE_KEY);
    }

    public static void greenLinePressed(Context context){

        //GoogleAnalytics
        Tracker t = getTracker(context, TrackerName.APP_TRACKER);
        t.setScreenName(SCHEDULES_KEY);
        t.send(new HitBuilders.EventBuilder()
                .setAction(GREEN_LINE_KEY)
                .build());

        //Parse
        ParseAnalytics.trackEventInBackground(GREEN_LINE_KEY);

        //Flurry
        FlurryAgent.logEvent(GREEN_LINE_KEY);
    }

    public static int getNumberOfOpens(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("Analytics", Context.MODE_PRIVATE);
        return preferences.getInt("number_of_app_opens", 0);
    }

    private static void incrementNumberOfOpens(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("Analytics", Context.MODE_PRIVATE);
        int numberOfOpens = preferences.getInt("number_of_app_opens", 0);
        numberOfOpens++;
        preferences.edit().putInt("number_of_app_opens", numberOfOpens).apply();
    }

    public static synchronized Tracker getTracker(Context context, TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(GA_PROPERTY_ID) : null;
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    protected static String numberGroup(int number){
        if (number == 1){
            return "1";
        }else if (number < 6){
            return "2-5";
        }else if (number < 10){
            return "6-10";
        }else if (number < 20){
            return "11-20";
        }else if (number < 50){
            return "21-50";
        }else if (number < 100){
            return "51-100";
        }
        return "100+";
    }
}
