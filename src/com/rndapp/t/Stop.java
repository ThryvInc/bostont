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
     * Reads values for the stop's name and
     * @param stop
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

    public long minSec() {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < seconds.size(); i++) {
            Long l = seconds.get(i);
            if (l < min) {
                min = l;
            }
        }
        return min;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Stop.class)
            return false;
        if (!((Stop) o).name.equalsIgnoreCase(this.name))
            return false;
        return true;
    }

}
