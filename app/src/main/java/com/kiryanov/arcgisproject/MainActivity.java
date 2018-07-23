package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private MapView mapView;
    private FolderOverlay mainFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView(savedInstanceState);

//        geoPoints = new ArrayList<>();
//        parseGeoJson(getString(R.string.geo_json_1), geoPoints);

        KmlFeature.Styler styler = new KmlFeature.Styler() {
            @Override
            public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

            }

            @Override
            public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {

            }

            @Override
            public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {

            }

            @Override
            public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
                polygon.setFillColor(Color.BLUE);
            }

            @Override
            public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

            }
        };
        KmlDocument kmlDocument = new KmlDocument();
        kmlDocument.parseGeoJSON(getString(R.string.geo_json_1));
        mainFolder = (FolderOverlay) kmlDocument.mKmlRoot
                .buildOverlay(mapView, null, styler, kmlDocument);
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

    private void simplePolygon(final MapView osmMap) {

    }

    private Polygon pathOverlay = null;
    private void updateRoute(final MapView osmMap, final int color){
        updateThread = new Thread(() -> {
            final ArrayList<GeoPoint> zoomPoints = new ArrayList<>(geoPoints);
            int MAX_POINTS = 1000;

            //Remove any points that are offscreen
            removeHiddenPoints(osmMap, zoomPoints);

            //If there's still too many then thin the array
            if (zoomPoints.size() > MAX_POINTS){
                simpleOverlay(zoomPoints, MAX_POINTS);
            }

            //Update the map on the event thread
            osmMap.post(() -> {
                //ideally the Polyline construction would happen in the thread but that causes glitches while the event thread
                //waits for redraw:
                osmMap.getOverlays().remove(pathOverlay);
                pathOverlay = new Polygon(osmMap);
                pathOverlay.setPoints(zoomPoints);
                osmMap.getOverlayManager().add(pathOverlay);
                osmMap.invalidate();
            });
        });
        updateThread.start();
    }

    private void removeHiddenPoints(MapView osmMap, ArrayList<GeoPoint> zoomPoints){
        BoundingBox bounds = osmMap.getBoundingBox();

        for (Iterator<GeoPoint> iterator = zoomPoints.iterator(); iterator.hasNext();) {
            GeoPoint point = iterator.next();

            boolean inLatitude = point.getLatitude() < bounds.getLatNorth() &&
                    point.getLatitude() > bounds.getLatSouth();

            boolean inLongitude = point.getLongitude() < bounds.getLonEast() &&
                    point.getLongitude() > bounds.getLonWest();

            if(!inLongitude || !inLatitude){
                iterator.remove();
            }
        }
    }

    private void simpleOverlay(ArrayList<GeoPoint> zoomPoints, int maxPoints) {
        int stepSize = (int) zoomPoints.size() / maxPoints;
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
                    second.getAsDouble(),
                    first.getAsDouble()
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
    }
}