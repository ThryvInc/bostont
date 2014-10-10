package com.rndapp.t;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rndapp.subway_lib.Line;
import com.rndapp.subway_lib.MainActivity;
import com.rndapp.subway_lib.Notification;

import org.json.JSONObject;

/**
 * A subclass of MainActivity in the subway_lib submodule.
 */
public class BostonTActivity extends MainActivity implements OnClickListener {

    public enum BostonLine implements Line {
        ORANGE("orange"),
        RED("red"),
        BLUE("blue");

        // cruft in html: e.g., http://developer.mbta.com/lib/rthr/blue.json
        private final String name;

        private BostonLine(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    protected final String getLineUrl(Line line) {
        return "http://developer.mbta.com/lib/rthr/" + line.getName() + ".json";
    }

    @Override
    protected final String getFlurryApiKey() {
        return "G9S4S9H9JXBK884NW625";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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

    @Override
    public void onLineScheduleLoaded(JSONObject jsonObject) {
        super.onLineScheduleLoaded(jsonObject);

        if (fetchedData != null) {
            ListView lv = (ListView) findViewById(R.id.line_list);
            ScheduleAdapter sa = new ScheduleAdapter(BostonTActivity.this, R.layout.item, fetchedData);
            lv.setAdapter(sa);
        }
    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        switch (v.getId()) {

            case R.id.orange_btn:
                makeRequest(BostonLine.ORANGE);
                return;

            case R.id.red_btn:
                makeRequest(BostonLine.RED);
                return;

            case R.id.blue_btn:
                makeRequest(BostonLine.BLUE);
                return;

            // TODO - alternative... there's no json schedule for green line
            case R.id.green_btn:
                startActivity(new Intent(this, Notification.class));
                return;
        }
        Toast.makeText(this, "What other View was clicked?", Toast.LENGTH_LONG).show();

    }

}