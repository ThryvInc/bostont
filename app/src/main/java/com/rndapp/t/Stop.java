package com.rndapp.t;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Stop implements Serializable {

    public String name;
    public ArrayList<Long> seconds;

    public Stop(String name) {
        super();
        seconds = new ArrayList<Long>();
        this.name = name;
    }

    /**
     * Constructs a {@code Stop} from a {@code JSONObject}.
     * Reads values for the stop's name and
     *
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
        if (seconds == null || seconds.isEmpty()) return Long.MAX_VALUE;
        return Collections.min(seconds);
    }

    public String getName() {
        return name;
    }

    // stops equal if they have same name
    @Override
    public boolean equals(Object o) {
        return o instanceof Stop && ((Stop) o).getName().equalsIgnoreCase(name);
    }

}
