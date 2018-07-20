package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private GraphicsOverlay overlay;

    private Button addPolygonBtn;
    private Button addMarkerBtn;
    private Button addGeoJsonBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map_view);
        initMapView();

        addPolygonBtn = findViewById(R.id.add_polygon);
        addMarkerBtn = findViewById(R.id.add_marker);
        addGeoJsonBtn = findViewById(R.id.from_geo_json);

        addPolygonBtn.setOnClickListener(v -> addPolygons());
        addMarkerBtn.setOnClickListener(v -> addMarkers());
        addGeoJsonBtn.setOnClickListener(v -> addFromGeoJson());
    }

    private void initMapView() {
        ArcGISMap map = new ArcGISMap(
                Basemap.Type.OPEN_STREET_MAP,
                47.3, 39.6,
                6
        );
        mapView.setMap(map);

        overlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(overlay);
    }

    private double polygonOffset = 0;

    private void addPolygons() {
        int count = 100;

        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.Style.CROSS, Color.BLUE, null
        );

        PointCollection points = new PointCollection(SpatialReferences.getWgs84());

        points.add(39.6, 47.2);
        for (int i = 0; i < count; i++) {
            double offset = ((double) i) / count;
            points.add(39.6 + offset, 47.2 + offset);
        }
        points.add(39.6, 47.2);

        Polygon polygon = new Polygon(points);
        overlay.getGraphics().add(new Graphic(polygon, fillSymbol));

        polygonOffset += 0.2;

        setButtonText(addPolygonBtn, count);

        if (polygonOffset > 80) polygonOffset = -80;
    }
    private double markerOffset = 0;

    private void addMarkers() {
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat
                .getDrawable(this, R.mipmap.ic_launcher_round);
        final PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(drawable);
        markerSymbol.setHeight(50);
        markerSymbol.setWidth(50);
        markerSymbol.loadAsync();

        markerSymbol.addDoneLoadingListener(() -> {
            Random random = new Random();
            int count = 1000;
            int accuracy = 10000;
            double density = 0.5;

            for (int i = 0; i < count; i++) {
                double offsetX = (((double) random.nextInt() * random.nextInt()) / accuracy) % density;
                double offsetY = (((double) random.nextInt() * random.nextInt()) / accuracy) % density;

                Point point = new Point(
                        39.6 + offsetX + markerOffset,
                        47.2 + offsetY + markerOffset,
                        SpatialReferences.getWgs84()
                );
                Graphic graphic = new Graphic(point, markerSymbol);
                overlay.getGraphics().add(graphic);
            }

            if (markerOffset > 80) markerOffset = -80;
            markerOffset += density;

            setButtonText(addMarkerBtn, count);
        });
    }

    private Thread computate;
    private void addFromGeoJson() {
        Handler handler = new Handler(getMainLooper());

        int count = 100;

        String geoJson = getString(R.string.geo_json_3);

        if (computate == null || !computate.isAlive()) {
            jsonOffsetX = 0;
            jsonOffsetY += 0.3;

            computate = parseGeoJson(handler, geoJson, count);
            computate.start();
        } else {
            Toast.makeText(this, "Wait", Toast.LENGTH_SHORT).show();
        }
    }

    private double jsonOffsetX = 0;
    private double jsonOffsetY = 0;
    private Thread parseGeoJson(Handler handler, String geoJson, int count) {
        return new Thread(() -> {

            for (int i = 0; i < count; i++) {
                Log.d("Thread", "1: " + Thread.currentThread().getName());

                JsonObject object = new JsonParser().parse(geoJson).getAsJsonObject();
                JsonArray features = object.getAsJsonArray("features");

                for (JsonElement fe : features) {
                    JsonObject fo = fe.getAsJsonObject();

                    JsonArray coordinates = fo
                            .getAsJsonObject("geometry")
                            .getAsJsonArray("coordinates")
                            .get(0).getAsJsonArray();

                    PointCollection points = new PointCollection(SpatialReferences.getWgs84());

                    for (JsonElement element : coordinates)
                        addPoint(element, points);
                    addPoint(coordinates.get(0), points);

                    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(
                            SimpleFillSymbol.Style.DIAGONAL_CROSS,
                            i % 2 == 0 ? Color.RED : Color.BLUE,
                            null
                    );
                    Polygon polygon = new Polygon(points);

                    handler.post(() -> {
                        Log.d("Thread", "2: " + Thread.currentThread().getName());
                        overlay.getGraphics().add(new Graphic(polygon, fillSymbol));
                    });
                }

                handler.post(() -> {
                    jsonOffsetX += 0.05;
                    setButtonText(addGeoJsonBtn, 3);
                });
            }
        });
    }

    private void addPoint(JsonElement element, PointCollection points) {
        JsonArray coord = element.getAsJsonArray();

        JsonElement first = coord.get(0);
        JsonElement second = coord.get(1);

        if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
            points.add(new Point(
                    first.getAsDouble() + jsonOffsetX,
                    second.getAsDouble() + jsonOffsetY)
            );
        }
    }

    private void setButtonText(Button btn, int addable) {
        btn.setText(
                String.valueOf(Integer.parseInt(btn.getText().toString()) + addable)
        );
    }

    @Override
    protected void onResume() {
        mapView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.dispose();
        super.onDestroy();
    }
}
