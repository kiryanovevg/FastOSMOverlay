package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button addPolygonBtn;
    private Button addMarkerBtn;
    private Button addGeoJsonBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView();

        addPolygonBtn = findViewById(R.id.add_polygon);
        addMarkerBtn = findViewById(R.id.add_marker);
        addGeoJsonBtn = findViewById(R.id.from_geo_json);

        addPolygonBtn.setOnClickListener(v -> addPolygons());
        addMarkerBtn.setOnClickListener(v -> addMarkers());
        addGeoJsonBtn.setOnClickListener(v -> addFromGeoJson());
    }

    private void initMapView() {

    }

    private void addPolygons() {

    }

    private void addMarkers() {

    }

    private void addFromGeoJson() {

    }

    private void setButtonText(Button btn, int addable) {
        btn.setText(
                String.valueOf(Integer.parseInt(btn.getText().toString()) + addable)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
