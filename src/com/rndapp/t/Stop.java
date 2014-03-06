package com.rndapp.t;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Stop implements Serializable {

    /**
     * The name of the {@code Stop}.
     */
    private String name;

    /**
     * Seconds until the {@code Stop} is reached.
     */
    private Long seconds;

    /**
     * JSON key that maps to the {@code Stop}'s name.
     */
    final static String JSON_KEY_STOP_NAME = "Stop";

    /**
     * JSON key that maps to the {@code Stop}'s estimated time of arrival (in seconds).
     */
    final static String JSON_KEY_STOP_ETA = "Seconds";

    /**
     * Constructs a {@code Stop}.
     *
     * @param name The name of the {@code Stop}.
     */
    public Stop(String name) {
        super();
        this.name = name;
    }

    /**
     * Constructs a {@code Stop} from a {@code JSONObject}.
     * Reads values for the {@code Stop}'s name and
     *
     * @param stop The {@code JSONObject} that holds this {@code Stop}'s
     *             name and the duration to this {@code Stop}.
     */
    public Stop(JSONObject stop) {
        super();
        try {
            this.name = stop.getString(JSON_KEY_STOP_NAME);
            this.seconds = stop.getLong(JSON_KEY_STOP_ETA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if this {@code Stop} is equal to another {@code Stop}.
     *
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

    /**
     * Returns the {@code Stop}'s name.
     *
     * @return the {@code Stop}'s name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@code Stop}'s estimated time of arrival in seconds.
     *
     * @return the {@code Stop}'s estimated time of arrival in seconds.
     */
    public Long getSeconds() {
        return seconds;
    }

    /**
     * Sets the {@code Stop}'s estimated time of arrival in seconds.
     * If the ETA is already set, it takes the lesser (i.e., sooner) ETA.
     *
     * @param seconds the {@code Stop}'s new estimated time of arrival in seconds.
     */
    public void setSeconds(Long seconds) {
        this.seconds = (this.seconds == null) ? seconds : Math.min(this.seconds, seconds);
    }


}
