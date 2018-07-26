package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private static final String URL = "";

    private MapView mapView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.loading);

        initMapView(savedInstanceState);
        initGeoJson();
    }

    private void initMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(8d);
        mapView.getController().setCenter(new GeoPoint(LAT, LNG));

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });
    }

    private void initGeoJson() {
        Handler handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        StringBuilder builder = new StringBuilder();

        new Thread(() -> {
            try {
                URL url = new URL(URL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new URL(URL).openConnection().getInputStream()
                ));

                while (reader.ready()) {
                    builder.append(reader.readLine());
                }

                reader.close();

                handler.post(() -> onComplete(builder.toString()));

            } catch (IOException e) {
                e.printStackTrace();

                handler.post(this::onError);
            }
        }).start();
    }

    private void onComplete(String geoJson) {

    }

    private void onError() {

    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}