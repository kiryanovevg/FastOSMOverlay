package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FourthActivity extends AppCompatActivity {/*

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private Handler handler;
    private MapView mapView;
    private ProgressBar progressBar;
    private Button button;

    private FolderOverlay polygonOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());

        progressBar = findViewById(R.id.loading);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> initGeoJson());

        initMapView(savedInstanceState);
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

        polygonOverlay = new FolderOverlay();
        mapView.getOverlays().add(polygonOverlay);
    }

    private Thread loadGeoJsonThread;
    private void initGeoJson() {
        if (loadGeoJsonThread == null || !loadGeoJsonThread.isAlive()) {

            StringBuilder builder = new StringBuilder();
            progressBar.setVisibility(View.VISIBLE);

            loadGeoJsonThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new URL(URL_DISTRICTS).openConnection().getInputStream()
                    ));

                    String input;
                    while ((input = reader.readLine()) != null) {
                        builder.append(input);
                    }

                    reader.close();

                    handler.post(() -> {
                        Toast.makeText(
                                MainActivity.this,
                                "GeoJson loading",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

                    createOverlayFromGeoJson(builder.toString());

                    handler.post(() -> {
                        Toast.makeText(
                                MainActivity.this,
                                "Setting overlay",
                                Toast.LENGTH_SHORT
                        ).show();

                        progressBar.setVisibility(View.GONE);
                    });

                } catch (IOException e) {
                    e.printStackTrace();

                    handler.post(() -> {
                        onError(e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    });
                }
            });
            loadGeoJsonThread.start();
        } else {
            Toast.makeText(
                    this,
                    "Thread is already running",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    *//*private FolderOverlay createOverlayFromGeoJson(String geoJson) {
        FolderOverlay folderOverlay = new FolderOverlay();

        JsonArray features = new JsonParser().parse(geoJson).getAsJsonObject()
                .get("features").getAsJsonArray();

        for (JsonElement je1: features) {
            JsonObject feature = je1.getAsJsonObject();
            JsonArray coordinates = feature.getAsJsonObject("geometry")
                    .getAsJsonArray("coordinates");

            for (JsonElement je2 : coordinates) {
                JsonArray coordinate = je2.getAsJsonArray();


            }

        }

        return folderOverlay;
    }*//*

    private void createOverlayFromGeoJson(String geoJson) {
        List<Overlay> polygons = new ArrayList<>();

        JsonObject object = new JsonParser().parse(geoJson).getAsJsonObject();
        JsonArray features = object.getAsJsonArray("features");

        int count = 0;
        for (JsonElement fe : features) {
            JsonObject fo = fe.getAsJsonObject();

            JsonArray coordinates = fo
                    .getAsJsonObject("geometry")
                    .getAsJsonArray("coordinates")
                    .get(0).getAsJsonArray();

            List<GeoPoint> geoPoints = new ArrayList<>();

            geoPoints.clear();
            for (JsonElement element : coordinates)
                addPoint(element, geoPoints);

            Polygon polygon = new Polygon();
            polygon.setFillColor(Color.GRAY);
            polygon.setStrokeWidth(1f);
            polygon.setPoints(geoPoints);

            polygons.add(polygon);

            count++;
            if (count == 10) {
                handler.post(() -> {
                    polygonOverlay.getItems().addAll(polygons);
                    mapView.invalidate();
                });
                polygons.clear();
                count = 0;
            }
        }

        *//*handler.post(() -> {
            mapView.getOverlays().addAll(polygons);
        });*//*
    }

    private void addPoint(JsonElement element, List<GeoPoint> points) {
        JsonArray coord = element.getAsJsonArray();

        JsonElement first = coord.get(0);
        JsonElement second = coord.get(1);

        if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
            points.add(new GeoPoint(
                    second.getAsDouble(),
                    first.getAsDouble()
            ));
        }
    }

    private void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
    }*/
}