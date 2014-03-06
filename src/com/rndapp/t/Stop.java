package com.rndapp.t;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

public class Stop implements Serializable {

    /**
     * The name of the {@code Stop}.
     */
    public String name;

    /**
     * A list of
     */
    public ArrayList<Long> seconds;

    /**
     * Constructs a {@code Stop}.
     * @param name The name of the {@code Stop}.
     */
    public Stop(String name) {
        super();
        seconds = new ArrayList<Long>();
        this.name = name;
    }

    /**
     * Constructs a {@code Stop} from a {@code JSONObject}.
     * Reads values for the {@code Stop}'s name and
     * @param stop The {@code JSONObject} that holds this {@code Stop}'s
     *             name and the duration to this {@code Stop}.
     */
    public Stop(JSONObject stop) {
        super();
        seconds = new ArrayList<Long>();
        try {
            this.name = stop.getString("Stop");
            this.seconds.add(stop.getLong("Seconds"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the minimum time/duration in the list of seconds.
     * @return the minimum time/duration in the list of seconds.
     */
    public long minSec() {
        // TODO is this method necessary? When is there going to be more than one Long in seconds list?
        long min = Long.MAX_VALUE;
        for (int i = 0; i < seconds.size(); i++) {
            Long l = seconds.get(i);
            if (l < min) {
                min = l;
            }
        }
        return min;
    }

    /**
     * Checks if this {@code Stop} is equal to another {@code Stop}.
     * @param o The other {@code Stop}.
     * @return True if this {@code Stop} and the other {@code Stop}
     * have the same names.
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Stop.class)
            return false;
        return ((Stop) o).name.equalsIgnoreCase(this.name);
    }

}
