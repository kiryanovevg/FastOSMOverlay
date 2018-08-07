package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.Collection;
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

//    private FolderOverlay polygonOverlay;
    private PolygonFolder polygonFolder;

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

//        polygonOverlay = new FolderOverlay();
//        mapView.getOverlays().add(polygonOverlay);

        polygonFolder = new PolygonFolder();
        mapView.getOverlays().add(polygonFolder);
        mapView.addMapListener(new DelayedMapListener(polygonFolder, 200));
    }

    private Disposable disposable;
    private void initGeoJson() {
        if (disposable == null || disposable.isDisposed()) {
            Repository.getInstance().getDistricts(this, getToleranceForReduce())
                    .subscribe(new Observer<List<SimplePolygon>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNext(List<SimplePolygon> polygon) {
                            polygonFolder.getItems().addAll(polygon);
                            mapView.invalidate();
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.GONE);
                            showMessage(e.getMessage());
                            disposable.dispose();
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

    private double getToleranceForReduce() {
        BoundingBox boundingBox = mapView.getBoundingBox();
        final double latSpanDegrees = boundingBox.getLatitudeSpan();

        return latSpanDegrees / getResources().getDisplayMetrics().densityDpi;
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