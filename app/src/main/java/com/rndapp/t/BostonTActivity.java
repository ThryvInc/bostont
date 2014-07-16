package com.rndapp.t;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.rndapp.subway_lib.MainActivity;
import com.rndapp.subway_lib.Notification;
import com.rndapp.subway_lib.TouchImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

/**
 * A subclass of MainActivity in the subway_lib submodule.
 */
public class BostonTActivity extends MainActivity implements OnClickListener {

    private final String ORANGE = "orange";
    private final String RED = "red";
    private final String BLUE = "blue";

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
     *
     * @param lineColor
     */
    private void subwayLineButtonClicked(String lineColor) {
        pd = ProgressDialog.show(this, "", "Loading...", true, true);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                fetchedData = null;
                String sched = getSchedule(lineColor);
                try {
                    fetchedData = new JSONObject(sched);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Invokes handler's handleMessage, which creates new ScheduleAdapter
                handler.sendEmptyMessage(0);
            }
        });
        thread.start();
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
                subwayLineButtonClicked(ORANGE);
                break;

            case R.id.red_btn:
                subwayLineButtonClicked(RED);
                break;

            case R.id.blue_btn:
                subwayLineButtonClicked(BLUE);
                break;

            // TODO - alternative... there's no json schedule for green line
            case R.id.green_btn:
                startActivity(new Intent(this, Notification.class));
                break;

            default:
                Toast.makeText(this, "What other View was clicked?", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Returns the subway schedule for a given subway line.
     * The schedule is requested from a json file on the web.
     *
     * @param line The color of the subway line to retrieve (e.g., "orange").
     * @return A string containing the schedule.
     */
    protected String getSchedule(String line) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://developer.mbta.com/lib/rthr/" + line + ".json");
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