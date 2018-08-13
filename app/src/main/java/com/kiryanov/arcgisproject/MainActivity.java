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
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private Handler handler;
    private MapView mapView;
    private ProgressBar progressBar;

    private Button btnPoints;
    private Button btnPolygons;
    private Button btnClear;

//    private FolderOverlay polygonOverlay;
    private PolygonFolderTouch polygonFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());

        progressBar = findViewById(R.id.progress_bar);
        btnPoints = findViewById(R.id.btn_points);
        btnPolygons = findViewById(R.id.btn_polygons);
        btnClear = findViewById(R.id.btn_clear);

        btnPoints.setOnClickListener(v -> addMarkers());
        btnPolygons.setOnClickListener(v -> addPolygons());
        btnClear.setOnClickListener(v -> clearMap());

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

        polygonFolder = new PolygonFolderTouch();
        mapView.getOverlays().add(polygonFolder);
//        mapView.addMapListener(new DelayedMapListener(polygonFolder, 200));
    }

    private double markerOffset = 0;

    private void addMarkers() {
        int count = 5000;

        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);

            Marker marker = new Marker(mapView);
            marker.setIcon(getResources().getDrawable(R.drawable.moreinfo_arrow));
            marker.setPosition(new GeoPoint(
                    LAT + dispersionX + markerOffset, LNG + dispersionY
            ));

            mapView.getOverlays().add(marker);
        }

        if (markerOffset > 80) markerOffset = -80;
        markerOffset += density;
        setButtonText(btnPoints, count);
    }
    private Disposable disposable;

    private void addPolygons() {
        if (disposable == null || disposable.isDisposed()) {
            Repository.getInstance().getCustomPolygon(this, 600)
//            Repository.getInstance().getDistricts(this)
                    .subscribe(new Observer<List<Polygon>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNext(List<Polygon> polygon) {
                            polygonFolder.getItems().addAll(polygon);
                            mapView.invalidate();
                            setButtonText(btnPolygons, polygon.size());
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
    private void clearMap() {
        mapView.getOverlays().clear();
        mapView.invalidate();
    }

    //X, Y
    private double getDispersion(int random, int accuracy, double density) {
        return (((double) random * random) / accuracy) % density;
    }

    private double getToleranceForReduce() {
        BoundingBox boundingBox = mapView.getBoundingBox();
        final double latSpanDegrees = boundingBox.getLatitudeSpan();

        return latSpanDegrees / getResources().getDisplayMetrics().densityDpi;
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setButtonText(Button btn, int addable) {
        btn.setText(
                String.valueOf(Integer.parseInt(btn.getText().toString()) + addable)
        );
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