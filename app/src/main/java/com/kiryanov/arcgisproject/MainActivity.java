package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;

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
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    private void addMarkers() {
        Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.ic_launcher_foreground);

        for (int i = 0; i < ; i++) {

        }
    }

    private void addPolygons() {

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
        mapView.onDestroy();
        super.onDestroy();
    }
}
