package com.rndapp.t;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rndapp.subway_lib.MainActivity;
import com.rndapp.subway_lib.Notification;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A subclass of MainActivity in the subway_lib submodule.
 * Responsible for fetching JSON files from the Boston T website.
 */
public class BostonTActivity extends MainActivity implements OnClickListener {

    /**
     * Queue of Volley requests.
     */
    private final RequestQueue requestQueue = Volley.newRequestQueue(this);

    /**
     * The API key for this project.
     */
    private static final String API_KEY = "G9S4S9H9JXBK884NW625";

    /**
     * A modifiable set of name/value mappings.
     * Holds the schedule String.
     */
    private JSONObject fetchedData;

    /**
     * Shows a "Loading..." message when user clicks a subway line button.
     */
    private ProgressDialog pd;

    /**
     * Allows us to schedule {@code Message}s and {@code Runnable}s
     * to be executed at some point in the future. Enqueued objects
     * will be called by the current thread's {@code MessageQueue}
     * when they are received.
     */
    private Handler handler = new Handler() {
        /**
         * Populates the ListView.
         * @param msg The received message (usually just a blank 0).
         */
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            if (fetchedData != null) {
                ListView lv = (ListView) findViewById(R.id.line_list);
                ScheduleAdapter sa = new ScheduleAdapter(context, R.layout.item, fetchedData);
                lv.setAdapter(sa);
                va.setInAnimation(slideLeftIn);
                va.setOutAnimation(slideLeftOut);
                va.showNext();
            } else {
                Toast.makeText(context, "Please make sure you are connected to the internet.", Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        setXML();
    }

    /**
     * Assigns a background color and {@code OnClickListener} to each of the buttons.
     */
    @Override
    protected void setXML() {
        super.setXML();

        Button red = (Button) findViewById(R.id.red_btn);
        Button blue = (Button) findViewById(R.id.blue_btn);
        Button orange = (Button) findViewById(R.id.orange_btn);
        Button green = (Button) findViewById(R.id.green_btn);

        red.setOnClickListener(this);
        red.setBackgroundColor(getResources().getColor(R.color.red));
        blue.setOnClickListener(this);
        blue.setBackgroundColor(getResources().getColor(R.color.blue));
        orange.setOnClickListener(this);
        orange.setBackgroundColor(getResources().getColor(R.color.orange));
        green.setOnClickListener(this);
        green.setBackgroundColor(getResources().getColor(R.color.green));
    }

    /**
     * Manages all clicks on Views.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {

            case R.id.see_map:
                va.setInAnimation(slideRightIn);
                va.setOutAnimation(slideRightOut);
                va.showPrevious();
                break;

            case R.id.see_sched:
                va.setInAnimation(slideLeftIn);
                va.setOutAnimation(slideLeftOut);
                va.showNext();
                break;

            case R.id.back_to_sched:
                va.setInAnimation(slideRightIn);
                va.setOutAnimation(slideRightOut);
                va.showPrevious();
                break;

            case R.id.orange_btn:
                fetchData(Trip.ORANGE);
                break;

            case R.id.red_btn:
                fetchData(Trip.RED);
                break;

            case R.id.blue_btn:
                fetchData(Trip.BLUE);
                break;

            // TODO - alternative... there's no json schedule for green line
            case R.id.green_btn:
                // this just displays Green Line unavailability notification
                startActivity(new Intent(this, Notification.class));
                break;

            default:
                Toast.makeText(this, "What other View was clicked?", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Fetches data from the web. Called when a subway line is pressed.
     *
     * @param lineColor The line to fetch data for.
     */
    private void fetchData(final String lineColor) {
        pd = ProgressDialog.show(this, "", "Loading...", true, true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                fetchedData = null;
                getSchedule(lineColor);
                // Invokes handler's handleMessage, which creates new ScheduleAdapter
                handler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    /**
     * Returns the URL of a specific subway line.
     *
     * @param line The color of the subway line to retrieve (e.g., "orange").
     * @return the URL of the given line's JSON schedule. If line is "orange", then this method
     * returns "http://developer.mbta.com/lib/rthr/orange.json".
     */
    public String getURL(final String line) {
        return "http://developer.mbta.com/lib/rthr/" + line + ".json";
    }

    /**
     * Volley makes a GET request for JSON file and then saves the fetched JSON object.
     *
     * @param line The color of the subway line to retrieve (e.g., "orange").
     */
    private void getSchedule(final String line) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getURL(line),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject fetchedData) {
                        BostonTActivity.this.fetchedData = fetchedData;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }

    /**
     * Returns the subway schedule for a given subway line.
     * The schedule is requested from a json file on the web.
     *
     * @param line The color of the subway line to retrieve (e.g., "orange").
     * @return A string containing the schedule.
     */
    private String getScheduleWithHttp(final String line) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(getURL(line));
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String l;
                while ((l = reader.readLine()) != null) {
                    builder.append(l);
                }
            } else {
                //Log.e(ParseJSON.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * When this Activity starts, it also starts or continues
     * a Flurry session for the project denoted by the API key.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, API_KEY);
    }

    /**
     * When this Activity stops, it stops the Flurry session.
     */
    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}