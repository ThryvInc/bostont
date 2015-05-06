package com.rndapp.t.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rndapp.t.R;

import java.util.ArrayList;

/**
 * Created by ell on 2/8/15.
 */
public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationHolder> {

    Context context;
    int itemResID;
    Line mLine;

    protected boolean hasNorthbound;
    protected boolean hasSouthbound;
    protected boolean hasEastbound;
    protected boolean hasWestbound;

    public StationAdapter(Context context, int itemResID, Line line) {
        this.context = context;
        this.itemResID = itemResID;
        this.mLine = line;
    }

    public Station getItem(int stopsAway) {
        return mLine.mStations.get(stopsAway);
    }

    @Override
    public int getItemCount(){
        return mLine.mStations.size();
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
    public StationHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(itemResID, viewGroup, false);

        StationHolder stationHolder = new StationHolder(itemView);
        stationHolder.adapter = this;
        return stationHolder;
    }

    @Override
    public void onBindViewHolder(StationHolder holder, int position) {
        Station station = getItem(position);

        holder.station = station;

        holder.stationName.setText(station.getStationName());

        switch (station.getRoutes().size()){
            case 6:
                ArrayList<Prediction> predictions6 = station.getPredictionsForRoute(station.getRoutes().get(5));
                setupRoute(predictions6, holder.route6);
            case 5:
                ArrayList<Prediction> predictions5 = station.getPredictionsForRoute(station.getRoutes().get(4));
                setupRoute(predictions5, holder.route5);
            case 4:
                ArrayList<Prediction> predictions4 = station.getPredictionsForRoute(station.getRoutes().get(3));
                setupRoute(predictions4, holder.route4);
            case 3:
                ArrayList<Prediction> predictions3 = station.getPredictionsForRoute(station.getRoutes().get(2));
                setupRoute(predictions3, holder.route3);
            case 2:
                ArrayList<Prediction> predictions2 = station.getPredictionsForRoute(station.getRoutes().get(1));
                setupRoute(predictions2, holder.route2);
            case 1:
                ArrayList<Prediction> predictions1 = station.getPredictionsForRoute(station.getRoutes().get(0));
                setupRoute(predictions1, holder.route1);
        }

        showHideRoutes(station, holder);
    }

    private void setupRoute(ArrayList<Prediction> predictions, RouteHolder routeHolder){
        if (predictions.size() > 0){
            Prediction prediction = Prediction.earliestPrediction(predictions);
            Prediction otherPrediction = Prediction.earliestOtherDirectionPrediction(predictions, prediction);
            routeHolder.routeName.setText(prediction.mRoute.getRouteName().toUpperCase());
            switch (prediction.mRoute.getColorId()){
                case R.color.red:
                    routeHolder.routeName.setBackgroundResource(R.drawable.rounded_corner_red);
                    break;
                case R.color.blue:
                    routeHolder.routeName.setBackgroundResource(R.drawable.rounded_corner_blue);
                    break;
                case R.color.orange:
                    routeHolder.routeName.setBackgroundResource(R.drawable.rounded_corner_orange);
                    break;
                case R.color.green:
                    routeHolder.routeName.setBackgroundResource(R.drawable.rounded_corner_green);
                    break;
            }

            setPrediction(prediction, routeHolder);
            setPrediction(otherPrediction, routeHolder);
        }
    }

    private void setPrediction(Prediction prediction, RouteHolder routeHolder){
        if (prediction != null){
            if (prediction.getDirectionName().equals("Northbound") || prediction.getDirectionName().equals("Westbound")){
                routeHolder.leftPrediction.setText(formattedPrediction(prediction));

                Drawable arrow = context.getResources().getDrawable( R.drawable.arrow_up_white );
                routeHolder.leftImageView.setImageDrawable(colorFilteredImage(prediction.getRoute().getColorId(), arrow));
            }
            if (prediction.getDirectionName().equals("Southbound") || prediction.getDirectionName().equals("Eastbound")){
                routeHolder.rightPrediction.setText(formattedPrediction(prediction));

                Drawable arrow = context.getResources().getDrawable( R.drawable.arrow_down_white );
                routeHolder.rightImageView.setImageDrawable(colorFilteredImage(prediction.getRoute().getColorId(), arrow));
            }
        }
    }

    private Drawable colorFilteredImage(int colorId, Drawable drawable){
        ColorFilter filter = new LightingColorFilter(Color.WHITE, context.getResources().getColor(colorId));
        drawable.setColorFilter(filter);
        return drawable;
    }

    private void showHideRoutes(Station station, StationHolder holder){
        switch (station.getRoutes().size()){
            case 6:
                holder.route6.setVisibility(View.VISIBLE);
            case 5:
                holder.route5.setVisibility(View.VISIBLE);
            case 4:
                holder.route4.setVisibility(View.VISIBLE);
            case 3:
                holder.route3.setVisibility(View.VISIBLE);
            case 2:
                holder.route2.setVisibility(View.VISIBLE);
            case 1:
                holder.route1.setVisibility(View.VISIBLE);
        }

        switch (station.getRoutes().size()){
            case 0:
                holder.route1.setVisibility(View.GONE);
            case 1:
                holder.route2.setVisibility(View.GONE);
            case 2:
                holder.route3.setVisibility(View.GONE);
            case 3:
                holder.route4.setVisibility(View.GONE);
            case 4:
                holder.route5.setVisibility(View.GONE);
            case 5:
                holder.route6.setVisibility(View.GONE);
        }
    }

    private String formattedPrediction(Prediction prediction) {
        int seconds = prediction.getPredictionInSeconds();
        int minute = seconds/60;
        return Integer.toString(minute) + "m";
    }

    static class StationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecyclerView.Adapter adapter;
        TextView stationName;
        ImageView refreshButton;
        ProgressBar progressBar;
        RouteHolder route1;
        RouteHolder route2;
        RouteHolder route3;
        RouteHolder route4;
        RouteHolder route5;
        RouteHolder route6;

        Station station;

        public StationHolder(View itemView) {
            super(itemView);
            View routeNames = itemView.findViewById(R.id.route_names);
            refreshButton = (ImageView)itemView.findViewById(R.id.btn_refresh);
            progressBar = (ProgressBar)itemView.findViewById(R.id.pb_refresh);
            View leftArrows = itemView.findViewById(R.id.up_arrows);
            View rightArrows = itemView.findViewById(R.id.down_arrows);
            View leftPredictions = itemView.findViewById(R.id.up_predictions);
            View rightPredictions = itemView.findViewById(R.id.down_predictions);

            stationName = (TextView)itemView.findViewById(R.id.tv_station_name);
            refreshButton.setOnClickListener(this);

            route1 = new RouteHolder((TextView)routeNames.findViewById(R.id.route_name1),
                    (ImageView)leftArrows.findViewById(R.id.iv_arrow1),
                    (ImageView)rightArrows.findViewById(R.id.iv_arrow1),
                    (TextView)leftPredictions.findViewById(R.id.tv_prediction1),
                    (TextView)rightPredictions.findViewById(R.id.tv_prediction1));

            route2 = new RouteHolder((TextView)routeNames.findViewById(R.id.route_name2),
                    (ImageView)leftArrows.findViewById(R.id.iv_arrow2),
                    (ImageView)rightArrows.findViewById(R.id.iv_arrow2),
                    (TextView)leftPredictions.findViewById(R.id.tv_prediction2),
                    (TextView)rightPredictions.findViewById(R.id.tv_prediction2));

            route3 = new RouteHolder((TextView)routeNames.findViewById(R.id.route_name3),
                    (ImageView)leftArrows.findViewById(R.id.iv_arrow3),
                    (ImageView)rightArrows.findViewById(R.id.iv_arrow3),
                    (TextView)leftPredictions.findViewById(R.id.tv_prediction3),
                    (TextView)rightPredictions.findViewById(R.id.tv_prediction3));

            route4 = new RouteHolder((TextView)routeNames.findViewById(R.id.route_name4),
                    (ImageView)leftArrows.findViewById(R.id.iv_arrow4),
                    (ImageView)rightArrows.findViewById(R.id.iv_arrow4),
                    (TextView)leftPredictions.findViewById(R.id.tv_prediction4),
                    (TextView)rightPredictions.findViewById(R.id.tv_prediction4));

            route5 = new RouteHolder((TextView)routeNames.findViewById(R.id.route_name5),
                    (ImageView)leftArrows.findViewById(R.id.iv_arrow5),
                    (ImageView)rightArrows.findViewById(R.id.iv_arrow5),
                    (TextView)leftPredictions.findViewById(R.id.tv_prediction5),
                    (TextView)rightPredictions.findViewById(R.id.tv_prediction5));

            route6 = new RouteHolder((TextView)routeNames.findViewById(R.id.route_name6),
                    (ImageView)leftArrows.findViewById(R.id.iv_arrow6),
                    (ImageView)rightArrows.findViewById(R.id.iv_arrow6),
                    (TextView)leftPredictions.findViewById(R.id.tv_prediction6),
                    (TextView)rightPredictions.findViewById(R.id.tv_prediction6));
        }

        @Override
        public void onClick(View v) {
            refreshButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            station.refresh(new Station.OnStationRefreshCallback() {
                @Override
                public void onStationRefreshSuccess() {
                    if (adapter != null) adapter.notifyDataSetChanged();
                    refreshButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onStationRefreshFailure() {
                    refreshButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    static class RouteHolder {
        TextView routeName;
        ImageView leftImageView;
        ImageView rightImageView;
        TextView leftPrediction;
        TextView rightPrediction;

        RouteHolder(TextView routeName, ImageView leftImageView, ImageView rightImageView, TextView leftPrediction, TextView rightPrediction) {
            this.routeName = routeName;
            this.leftImageView = leftImageView;
            this.rightImageView = rightImageView;
            this.leftPrediction = leftPrediction;
            this.rightPrediction = rightPrediction;
        }

        public void setVisibility(int visibility){
            routeName.setVisibility(visibility);
            leftImageView.setVisibility(visibility);
            rightImageView.setVisibility(visibility);
            leftPrediction.setVisibility(visibility);
            rightPrediction.setVisibility(visibility);
        }
    }
}
