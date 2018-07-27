package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private static final String URL = "https://gisro.donland.ru/api/vector_layers/1/records/?polygonbox=POLYGON((45.4833984375%2051.364921488259526,%2045.4833984375%2044.6061127451739,%2035.496826171875%2044.6061127451739,%2035.496826171875%2051.364921488259526,45.4833984375%2051.364921488259526))";

    private Handler handler;
    private MapView mapView;
    private ProgressBar progressBar;
    private Button button;

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
    }

    private Thread loadGeoJsonThread;
    private void initGeoJson() {
        if (loadGeoJsonThread == null || !loadGeoJsonThread.isAlive()) {

            StringBuilder builder = new StringBuilder();
            progressBar.setVisibility(View.VISIBLE);

            loadGeoJsonThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new URL(URL).openConnection().getInputStream()
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

                    FolderOverlay overlay = createOverlayFromGeoJson(builder.toString());

                    handler.post(() -> {
                        Toast.makeText(
                                MainActivity.this,
                                "Setting folder",
                                Toast.LENGTH_SHORT
                        ).show();

                        mapView.getOverlays().add(overlay);
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
            Toast.makeText(this, "Thread is already running", Toast.LENGTH_SHORT).show();
        }
    }

    private FolderOverlay createOverlayFromGeoJson(String geoJson) {
        KmlDocument document = new KmlDocument();
        document.parseGeoJSON(geoJson);

        FolderOverlay folderOverlay = ((FolderOverlay) document.mKmlRoot.buildOverlay(
                mapView, null, new KmlFeature.Styler() {

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
                        polygon.setFillColor(Color.GRAY);
                        polygon.setStrokeWidth(1.5f);
                    }

                    @Override
                    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

                    }
                }, document));

        return folderOverlay;
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
    }
}