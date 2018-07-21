package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double LAT = 47.2;
    private static final double LNG = 41;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private Button addPolygonBtn;
    private Button addMarkerBtn;
    private Button addGeoJsonBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView(savedInstanceState);

        addMarkerBtn = findViewById(R.id.add_marker);
        addPolygonBtn = findViewById(R.id.add_polygon);
        addGeoJsonBtn = findViewById(R.id.from_geo_json);

        addMarkerBtn.setOnClickListener(v -> addMarkers());
        addPolygonBtn.setOnClickListener(v -> addPolygons());
        addGeoJsonBtn.setOnClickListener(v -> addFromGeoJson());
    }

    private void initMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(LAT, LNG))
                .zoom(5)
                .build()
        );

    }

    private double markerOffset = 0;
    private void addMarkers() {
        int count = 1000;

        Icon icon = IconFactory.getInstance(this).fromResource(R.mipmap.ic_icon);

        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);

            mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            LAT + markerOffset + dispersionX,
                            LNG + markerOffset + dispersionY))
                    .icon(icon)
            );
        }

        if (markerOffset > 80) markerOffset = -80;
        markerOffset += density;

        setButtonText(addMarkerBtn, count);
    }

    double polygonOffsetX = 0;
    double polygonOffsetY = 0;
    private void addPolygons() {
        Handler handler = new Handler(getMainLooper());
        int count = 100;

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                List<LatLng> points = new ArrayList<>();
                parseGeoJson(getString(R.string.geo_json_1), points);

                int color = i % 2 == 0 ? Color.BLUE : Color.RED;

                handler.post(() -> {
                    mapboxMap.addPolygon(new PolygonOptions()
                            .addAll(points)
                            .fillColor(color)
                    );

                    polygonOffsetX += 0.1;

                    setButtonText(addPolygonBtn, 1);
                });
            }

            handler.post(() -> {
                polygonOffsetX = 0;
                polygonOffsetY += 0.3;
            });
        }).start();
    }

    private void parseGeoJson(String geoJson, List<LatLng> points) {
        JsonObject object = new JsonParser().parse(geoJson).getAsJsonObject();
        JsonArray features = object.getAsJsonArray("features");

        for (JsonElement fe : features) {
            JsonObject fo = fe.getAsJsonObject();

            JsonArray coordinates = fo
                    .getAsJsonObject("geometry")
                    .getAsJsonArray("coordinates")
                    .get(0).getAsJsonArray();

            for (JsonElement element : coordinates)
                addPoint(element, points);
            addPoint(coordinates.get(0), points);

        }
    }

    private void addPoint(JsonElement element, List<LatLng> points) {
        JsonArray coord = element.getAsJsonArray();

        JsonElement first = coord.get(0);
        JsonElement second = coord.get(1);

        if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
            points.add(new LatLng(
                    second.getAsDouble() + polygonOffsetY,
                    first.getAsDouble() + polygonOffsetX)
            );
        }
    }

    private void addFromGeoJson() {

    }

    private void setButtonText(Button btn, int addable) {
        btn.setText(
                String.valueOf(Integer.parseInt(btn.getText().toString()) + addable)
        );
    }

    //X, Y
    private double getDispersion(int random, int accuracy, double density) {
        return (((double) random * random) / accuracy) % density;
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
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}