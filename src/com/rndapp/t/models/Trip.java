package com.rndapp.t.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class to store a trip's line color, destination, and stops along the way to said destination.
 * For now, trip sequences are stored as private final static String arrays.
 */
public class Trip implements Serializable {

    /**
     * Key for the Blue line.
     */
    public final static String BLUE = "blue";

    /**
     * Key for the Orange line.
     */
    public final static String ORANGE = "orange";

    /**
     * Key for the Red line.
     */
    public final static String RED = "red";

    /**
     * Key for the Green line.
     */
    public final static String GREEN = "green";

    /**
     * The subway line the trip is on.
     */
    private String mLine;

    /**
     * JSON key that maps to the {@code Trip}'s destination.
     */
    private final static String JSON_KEY_TRIP_DESTINATION = "Destination";

    /**
     * JSON key that maps to the {@code Trip}'s list of predicted ETAs for {@code Stop}s.
     */
    private final static String JSON_KEY_TRIP_PREDICTIONS = "Predictions";

    /**
     * The trip's destination.
     */
    private String mDestination;

    /**
     * A list of {@code Stop}s along the way to the destination.
     */
    private ArrayList<Stop> mStops;

    /**
     * Constructs a {@code Trip}.
     *
     * @param trip The {@code JSONObject} that contains the {@code Trip}'s
     *             destination and a list of predictions.
     * @param line The {@code Trip}'s line color.
     */
    public Trip(final JSONObject trip, final String line) {
        super();
        mDestination = "";
        this.mLine = line;
        mStops = new ArrayList<Stop>();
        try {
            this.mDestination = trip.getString(JSON_KEY_TRIP_DESTINATION);
            initStops();
            incorporatePredictions(trip.getJSONArray(JSON_KEY_TRIP_PREDICTIONS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a specified JSONObject from a JSONArray.
     *
     * @param array The given JSONArray.
     * @param index The index of the JSONObject to retrieve.
     * @return a specified JSONObject from a JSONArray.
     */
    private JSONObject getJSONObjectFromArray(final JSONArray array, final int index) {
        JSONObject obj = null;
        try {
            obj = array.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Updates this {@code Trip}'s stops from an array of predictions.
     * @param predictions An array of stops and their predicted seconds.
     */
    public void incorporatePredictions(final JSONArray predictions) {
        // For each prediction...
        for (int i = 0; i < predictions.length(); i++) {

            // We haven't added the prediction yet...
            boolean added = false;

            final JSONObject prediction = getJSONObjectFromArray(predictions, i);
            final Stop predictedStop = new Stop(prediction);

            for (Stop stop : mStops) {
                try {
                    // If the stops have the same destination...
                    if (predictedStop.equals(stop)) {
                        // Set the stop's predicted ETA.
                        stop.setSeconds(prediction.getLong(Stop.JSON_KEY_STOP_ETA));
                        added = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!added) {
                mStops.add(predictedStop);
            }
        }
    }

    /**
     * Returns this {@code Trip}'s list of stops.
     *
     * @return this {@code Trip}'s list of stops.
     */
    public ArrayList<Stop> getStops() {
        return mStops;
    }

    /**
     * Returns this {@code Trip}'s destination.
     *
     * @return this {@code Trip}'s destination.
     */
    public String getDestination() {
        return mDestination;
    }

    /**
     * Checks if this {@code Trip} is equal to another {@code Trip}.
     *
     * @param o The other {@code Trip}.
     * @return True if this {@code Trip} and the other {@code Trip}
     * have the same destinations.
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Trip.class)
            return false;
        return ((Trip) o).mDestination.equalsIgnoreCase(this.mDestination);
    }

    /**
     * Populates the list of stops, depending on this {@code Trip}'s destination.
     */
    private void initStops() {
        String[][] lines = {
                BLUE_LINE,
                ORANGE_LINE,
                // RED - braintree to ashmont
                RED_BRAINTREE_ASHMONT,
                // RED - stops at braintree
                RED_BRAINTREE,
                // RED - straight to ashmont
                RED_ASHMONT
        };

        String[] list = {""};
        if (mLine.equalsIgnoreCase(BLUE)) {
            list = lines[0];
        } else if (mLine.equalsIgnoreCase(ORANGE)) {
            list = lines[1];
        } else if (mLine.equalsIgnoreCase(RED)) {
            if (mDestination.equalsIgnoreCase("Alewife")) {
                list = lines[2];
            } else if (mDestination.equalsIgnoreCase("Braintree")) {
                list = lines[3];
            } else if (mDestination.equalsIgnoreCase("Ashmont")) {
                list = lines[4];
            }
        }

        /* If we're going to the first stop, add stops in reverse. */
        if (mDestination.equalsIgnoreCase(list[0])) {
            for (int i = 0; i < list.length; i++) {
                mStops.add(new Stop(list[list.length - 1 - i]));
            }
        }

        /* If we're going to the last stop, add stops in normal order. */
        else {
            for (String stop : list) {
                mStops.add(new Stop(stop));
            }
        }
    }

    /**
     * The entire orange line.
     */
    private final static String[] ORANGE_LINE =
            {"Oak Grove",
                    "Malden Center",
                    "Wellington",
                    "Sullivan",
                    "Community College",
                    "North Station",
                    "Haymarket",
                    "State Street",
                    "Downtown Crossing",
                    "Chinatown",
                    "Tufts Medical",
                    "Back Bay",
                    "Mass Ave",
                    "Ruggles",
                    "Roxbury Crossing",
                    "Jackson Square",
                    "Stony Brook",
                    "Green Street",
                    "Forest Hills"};

    /**
     * The entire blue line.
     */
    private final static String[] BLUE_LINE =
            {"Wonderland",
                    "Revere Beach",
                    "Beachmont",
                    "Suffolk Downs",
                    "Orient Heights",
                    "Wood Island",
                    "Airport",
                    "Maverick",
                    "Aquarium",
                    "State Street",
                    "Government Center",
                    "Bowdoin"};

    /**
     * Red line, from Alewife to Braintree, then to Ashmont.
     */
    private final static String[] RED_BRAINTREE_ASHMONT =
            {"Alewife",
                    "Davis",
                    "Porter Square",
                    "Harvard Square",
                    "Central Square",
                    "Kendall/MIT",
                    "Charles/MGH",
                    "Park Street",
                    "Downtown Crossing",
                    "South Station",
                    "Broadway",
                    "Andrew",
                    "JFK/UMass",
                    "North Quincy",
                    "Wollaston",
                    "Quincy Center",
                    "Quincy Adams",
                    "Braintree",
                    "Savin Hill",
                    "Fields Corner",
                    "Shawmut",
                    "Ashmont"};

    /**
     * Red line, from Alewife to Braintree.
     */
    private final static String[] RED_BRAINTREE =
            {"Alewife",
                    "Davis",
                    "Porter Square",
                    "Harvard Square",
                    "Central Square",
                    "Kendall/MIT",
                    "Charles/MGH",
                    "Park Street",
                    "Downtown Crossing",
                    "South Station",
                    "Broadway",
                    "Andrew",
                    "JFK/UMass",
                    "North Quincy",
                    "Wollaston",
                    "Quincy Center",
                    "Quincy Adams",
                    "Braintree"};

    /**
     * Red line, from Alewife to Ashmont.
     */
    private final static String[] RED_ASHMONT =
            {"Alewife",
                    "Davis",
                    "Porter Square",
                    "Harvard Square",
                    "Central Square",
                    "Kendall/MIT",
                    "Charles/MGH",
                    "Park Street",
                    "Downtown Crossing",
                    "South Station",
                    "Broadway",
                    "Andrew",
                    "JFK/UMass",
                    "Savin Hill",
                    "Fields Corner",
                    "Shawmut",
                    "Ashmont"};
}