package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import static com.kiryanov.arcgisproject.Repository.LAT;
import static com.kiryanov.arcgisproject.Repository.LNG;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private MapView mapView;
    private ProgressBar progressBar;

    private Button btnPoints;
    private Button btnPolygons;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());

        progressBar = findViewById(R.id.progress_bar);
        btnPoints = findViewById(R.id.btn_points);
        btnPolygons = findViewById(R.id.btn_polygons);
        btnClear = findViewById(R.id.btn_clear);

        btnPoints.setOnClickListener(v -> addPoints());
//        btnPolygons.setOnClickListener(v -> addPolygons());
        btnClear.setOnClickListener(v -> clearMap());

        initMapView(savedInstanceState);
    }

    private void initMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(8d);
        mapView.getController().setCenter(new GeoPoint(LAT, LNG));
    }

    private void addPoints() {
        int count = 10000;

        Repository.getInstance().addPoints(
                count, mapView,
                () -> setButtonText(btnPoints, count)
        );
    }

    private void clearMap() {
        mapView.getOverlays().clear();
        mapView.invalidate();

        btnPoints.setText("0");
        btnPolygons.setText("0");
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