package com.kiryanov.arcgisproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    /*private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private MapView mapView;

    private Button addPolygonBtn;
    private Button addMarkerBtn;
    private Button addGeoJsonBtn;

    private List<GeoPoint> routeGeoPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView(savedInstanceState);

        addMarkerBtn = findViewById(R.id.add_marker);
        addPolygonBtn = findViewById(R.id.add_polygon);
        addGeoJsonBtn = findViewById(R.id.from_geo_json);

//        addMarkerBtn.setOnClickListener(v -> addMarkers());
//        addPolygonBtn.setOnClickListener(v -> addPolygons());
//        addGeoJsonBtn.setOnClickListener(v -> addFromGeoJson());

        routeGeoPoints = new ArrayList<>();
//        parseGeoJson(getString(R.string.geo_json_simple), routeGeoPoints);

        double offset = 0;
        for (int i = 0; i < 10000; i++) {
            routeGeoPoints.add(new GeoPoint(
                    LAT + offset,
                    LNG + offset
            ));

            offset += 0.05;
        }

        *//*pathOverlay = new Polyline(mapView);
        pathOverlay.setPoints(routeGeoPoints);
        pathOverlay.setColor(Color.CYAN);
        mapView.getOverlays().add(pathOverlay);
        mapView.invalidate();*//*

    }

    private void initMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(10d);
        mapView.getController().setCenter(new GeoPoint(LAT, LNG));

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                drawRoute(mapView, Color.RED);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                drawRoute(mapView, Color.BLUE);
                return false;
            }
        });
    }

    private Thread updateThread = null;
    public void drawRoute(final MapView osmMap, final int color){
        if(updateThread == null || !updateThread.isAlive()) {
            updateRoute(osmMap, color);
        }
    }

    private int MAX_POINTS = 150;
    private Polyline pathOverlay = null;
    private void updateRoute(final MapView osmMap, final int color){
        updateThread = new Thread(() -> {
            final ArrayList<GeoPoint> zoomPoints = new ArrayList<>(routeGeoPoints);

            //Remove any points that are offscreen
            removeHiddenPoints(osmMap, zoomPoints);

            //If there's still too many then thin the array
            if(zoomPoints.size() > MAX_POINTS){
                int stepSize = (int) zoomPoints.size()/MAX_POINTS;
                int count = 1;
                for (Iterator<GeoPoint> iterator = zoomPoints.iterator(); iterator.hasNext();) {
                    iterator.next();

                    if(count != stepSize){
                        iterator.remove();
                    }else{
                        count = 0;
                    }

                    count++;
                }
            }

            //Update the map on the event thread
            osmMap.post(new Runnable() {
                public void run() {
                    //ideally the Polyline construction would happen in the thread but that causes glitches while the event thread
                    //waits for redraw:
                    osmMap.getOverlays().remove(pathOverlay);
                    pathOverlay = new Polyline(osmMap);
                    pathOverlay.setPoints(zoomPoints);
                    pathOverlay.setColor(color);
                    osmMap.getOverlays().add(pathOverlay);
                    osmMap.invalidate();
                }
            });
        });
        updateThread.start();
    }

    private void removeHiddenPoints(MapView osmMap, ArrayList<GeoPoint> zoomPoints){
        BoundingBox bounds = osmMap.getBoundingBox();

        for (Iterator<GeoPoint> iterator = zoomPoints.iterator(); iterator.hasNext();) {
            GeoPoint point = iterator.next();

            boolean inLongitude = point.getLatitude() < bounds.getCenterLatitude() && point.getLatitude() > bounds.getCenterLatitude();
            boolean inLatitude = point.getLongitude() > bounds.getCenterLongitude() && point.getLongitude() < bounds.getCenterLongitude();
            if(!inLongitude || !inLatitude){
                iterator.remove();
            }
        }
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
                    first.getAsDouble(),
                    second.getAsDouble()
            ));
        }
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