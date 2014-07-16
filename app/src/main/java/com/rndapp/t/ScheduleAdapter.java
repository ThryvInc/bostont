package com.rndapp.t;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleAdapter extends BaseAdapter {

    Context context;
    int itemResID;
    long time;
    int color;
    ArrayList<Trip> trips;

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
                for (Trip trip : this.trips) {
                    if (trip.destination.equalsIgnoreCase(jsontrip.getString("Destination"))) {
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

    private void setColor(String line) {
        if (line.equalsIgnoreCase("BLUE")) {
            color = context.getResources().getColor(R.color.blue);
        } else if (line.equalsIgnoreCase("orange")) {
            color = context.getResources().getColor(R.color.orange);
        } else if (line.equalsIgnoreCase("red")) {
            color = context.getResources().getColor(R.color.red);
        }
    }

    // sum of all stops in trip
    @Override
    public int getCount() {
        int result = 0;
        for (Trip trip : trips) {
            result += trip.stops.size();
        }
        return result;
    }

    /**
     * Returns the stop (or trip, if the stop ends on the completion of a trip)
     * that is a specified number of stops away.
     *
     * @param stopsAway The specified number of stops away.
     *
     * @return the specified stop.
     */
    @Override
    public Object getItem(int stopsAway) {
        Object item = null;
        int stopCount = 0;
        for (Trip trip : trips) {
            for (int j = 0; j < trip.stops.size(); j++) {
                Stop stop = trip.stops.get(j);
                if (stopCount == stopsAway) {
                    item = (j == 0) ? trip : stop;
                    return item;
                }
                stopCount++;
            }
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // for now you can't click on stops
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object o = getItem(position);

        StopHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(itemResID, parent, false);

            holder = new StopHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.trip_name);
            holder.name = (TextView) convertView.findViewById(R.id.stop_name);
            holder.nextTrain = (TextView) convertView.findViewById(R.id.stop_time);

            convertView.setTag(holder);
        } else {
            holder = (StopHolder) convertView.getTag();
        }

        if (o instanceof Stop) {
            Stop s = (Stop) o;

            holder.tv.setVisibility(View.GONE);
            holder.name.setText(s.name);
            holder.nextTrain.setText(formattedPredict(s));
        } else {
            Trip t = (Trip) o;
            Stop s = t.stops.get(0);

            holder.tv.setText("To " + t.destination);
            holder.tv.setVisibility(View.VISIBLE);
            holder.tv.setBackgroundColor(color);
            holder.name.setText(s.name);
            holder.nextTrain.setText(formattedPredict(s));
        }

        return convertView;
    }

    private String formattedPredict(Stop s) {
        String result = "";
        long min = s.minSec();
        if (min != Long.MAX_VALUE) {
            long current = Calendar.getInstance().getTimeInMillis();
            long offset = current / 1000 - time;
            long r = (min - offset) / 60;
            result = Long.toString(r) + "min.";
        }
        return result;
    }

    static class StopHolder {
        TextView tv;
        TextView name;
        TextView nextTrain;
    }
}
