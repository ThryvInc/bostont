package com.rndapp.t;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter {

    /**
     * The context from which the adapter was created.
     * Used for getting resources and views.
     */
    Context context;

    /**
     * The item resource ID. Used for layout inflation.
     */
    int itemResID;

    /**
     * When the
     */
    long time;
    int color;
    ArrayList<Trip> trips;

    /**
     *
     * @param context
     * @param itemResID
     * @param json
     */
    public ScheduleAdapter(Context context, int itemResID, JSONObject json) {
        this.context = context;
        this.itemResID = itemResID;
        trips = new ArrayList<Trip>();
        try {
            JSONObject tripList = json.getJSONObject("TripList");
            time = tripList.getLong("CurrentTime");
            setColor(tripList.getString("Line"));
            JSONArray trips = tripList.getJSONArray("Trips");
            for (int i = 0; i < trips.length(); i++) {
                JSONObject jsontrip = trips.getJSONObject(i);
                boolean added = false;
                for (int j = 0; j < this.trips.size(); j++) {
                    Trip trip = this.trips.get(j);
                    if (trip.getDestination().equalsIgnoreCase(jsontrip.getString("Destination"))) {
                        trip.incorporatePredictions(jsontrip.getJSONArray("Predictions"));
                        added = true;
                    }
                }
                if (!added) {
                    Trip t = new Trip(jsontrip, tripList.getString("Line"));
                    this.trips.add(t);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the color of the TextView that displays the destination.
     * @param line The line color.
     */
    private void setColor(String line) {
        if (line.equalsIgnoreCase(Trip.BLUE)) {
            color = context.getResources().getColor(R.color.blue);
        } else if (line.equalsIgnoreCase(Trip.ORANGE)) {
            color = context.getResources().getColor(R.color.orange);
        } else if (line.equalsIgnoreCase(Trip.RED)) {
            color = context.getResources().getColor(R.color.red);
        }
    }

    /**
     * Returns the sum of stops of all trips.
     * @return the sum of stops of all trips.
     */
    @Override
    public int getCount() {
        int result = 0;
        for (Trip trip : trips) {
            result += trip.getStops().size();
        }
        return result;
    }

    /**
     * Returns the {@code Stop} (or {@code Trip}, if the {@code Stop}
     * ends on the completion of a {@code Trip}) that is a specified
     * number of {@code Stop}s away.
     * @param stopsAway The specified number of {@code Stop}s away.
     * @return the specified {@code Stop}, or null if none is found.
     */
    @Override
    public Object getItem(int stopsAway) {
        int stopCount = 0;
        for (Trip trip : trips) {
            ArrayList<Stop> stops = trip.getStops();
            for (int j = 0; j < stops.size(); j++) {
                Stop stop = stops.get(j);
                if (stopCount == stopsAway) {
                    return (j == 0) ? trip : stop;
                }
                stopCount++;
            }
        }
        return null;
    }

    /**
     * A {@code Stop}'s ID is its position.
     * @param position The requested stop.
     * @return the position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Checks to see if a {@code Stop} at a specified index is enabled.
     * For now, you cannot click on the stops.
     * @param position The requested stop.
     * @return false.
     */
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // The trip or stop in question
        Object o = getItem(position);

        // holds TextViews
        StopHolder holder;

        // If we're initializing view for the first time, set TextViews...
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(itemResID, parent, false);

            holder = new StopHolder();
            holder.destination = (TextView) convertView.findViewById(R.id.trip_name);
            holder.name = (TextView) convertView.findViewById(R.id.stop_name);
            holder.nextTrain = (TextView) convertView.findViewById(R.id.stop_time);

            convertView.setTag(holder);
        }
        // Otherwise, we've made the view before... Get the tag
        else {
            holder = (StopHolder) convertView.getTag();
        }

        // If it's a stop...
        if (o.getClass() == Stop.class) {
            Stop s = (Stop) o;
            // destination is invisible since it's a stop...
            holder.destination.setVisibility(View.GONE);
            // display the name of the stop
            holder.name.setText(s.name);
            // show the prediction for the next train
            holder.nextTrain.setText(formattedPredict(s));
        }
        // If it's a trip...
        else {
            Trip t = (Trip) o;
            Stop s = t.getStops().get(0);
            // display trip's destination w/ line color
            holder.destination.setText("To " + t.getDestination());
            holder.destination.setVisibility(View.VISIBLE);
            holder.destination.setBackgroundColor(color);
            // display name of the first stop
            holder.name.setText(s.name);
            // show the prediction for the next train
            holder.nextTrain.setText(formattedPredict(s));
        }

        return convertView;
    }

    /**
     *
     * @param s
     * @return
     */
    private String formattedPredict(Stop s) {
        String result = "";
        // time to get to next stop
        long min = s.minSec();
        if (min != Long.MAX_VALUE) {
            long current = Calendar.getInstance().getTimeInMillis();
            // time is the triplist's current time in seconds
            // offset is the elapsed seconds since start of triplist
            long elapsedSinceStart = current / 1000 - time;
            long secondsLeft = (min - elapsedSinceStart) / 60;
            result = Long.toString(secondsLeft) + " min.";
        }
        return result;
    }

    /**
     * A class that holds three TextViews.
     */
    static class StopHolder {
        TextView destination;
        TextView name;
        TextView nextTrain;
    }
}
