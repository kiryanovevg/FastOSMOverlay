package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private MapView mapView;

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
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(10d);
        mapView.getController().setCenter(new GeoPoint(LAT, LNG));
    }

    private double markerOffset = 0;
    private void addMarkers() {
        int count = 1000;

        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);

            //TODO
        }

        if (markerOffset > 80) markerOffset = -80;
        markerOffset += density;

        setButtonText(addMarkerBtn, count);
    }

    double polygonOffsetX = 0;
    double polygonOffsetY = 0;
    private void addPolygons() {
        /*Handler handler = new Handler(getMainLooper());
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
        }).start();*/
    }

    private void parseGeoJson(String geoJson, List<GeoPoint> points) {
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

    private void addPoint(JsonElement element, List<GeoPoint> points) {
        JsonArray coord = element.getAsJsonArray();

        JsonElement first = coord.get(0);
        JsonElement second = coord.get(1);

        if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
            points.add(new GeoPoint(
                    first.getAsDouble() + polygonOffsetX,
                    second.getAsDouble() + polygonOffsetY
            ));
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
        super.onDestroy();
    }
}