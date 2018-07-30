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

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

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

    private Disposable disposable;
    private void initGeoJson() {
        if (disposable == null || disposable.isDisposed()) {
            Repository.getInstance().getDistricts(
                    Toast.makeText(this, "GeoJson loading", Toast.LENGTH_SHORT)
            )
                    .subscribe(new Observer<Overlay>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNext(Overlay overlay) {
                            mapView.getOverlays().add(overlay);
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.GONE);
                            showMessage(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            progressBar.setVisibility(View.GONE);
                            disposable.dispose();
                        }
                    });

        } else {
            showMessage("Already running");
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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