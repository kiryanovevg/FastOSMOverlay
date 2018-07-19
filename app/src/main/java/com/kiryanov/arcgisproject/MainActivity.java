package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

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

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private GraphicsOverlay overlay;

    private Button addPolygonBtn;
    private Button addMarkerBtn;

    private double offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map_view);
        initMapView();

        addPolygonBtn = findViewById(R.id.add_polygon);
        addMarkerBtn = findViewById(R.id.add_marker);

        addPolygonBtn.setOnClickListener(v -> addPolygons());
        addMarkerBtn.setOnClickListener(v -> addMarkers());
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

    private void addPolygons() {
        int count = 100;

        for (int i = 0; i < count; i++) {

            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(
                    SimpleFillSymbol.Style.CROSS, Color.BLUE, null
            );

            PointCollection points = new PointCollection(SpatialReferences.getWgs84());

            points.add(47 + offset, 39 + offset);
            for (double j = 0.01; j < 1; j++) {
                points.add(47 + j + offset, 39 + j + offset);
            }
            points.add(47 + offset, 39 + offset);


            Polygon polygon = new Polygon(points);
            overlay.getGraphics().add(new Graphic(polygon, fillSymbol));

            offset += 0.2;
        }

        addPolygonBtn.setText(
                String.valueOf(Integer.parseInt(addPolygonBtn.getText().toString()) + count)
        );

        if (offset > 80) offset = -80;
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

            addMarkerBtn.setText(
                    String.valueOf(Integer.valueOf(addMarkerBtn.getText().toString()) + count)
            );
        });
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

    private String string = "[\n" +
            "              39.94628906249999,\n" +
            "              46.195042108660154\n" +
            "            ],\n" +
            "            [\n" +
            "              43.385009765625,\n" +
            "              46.195042108660154\n" +
            "            ],\n" +
            "            [\n" +
            "              43.385009765625,\n" +
            "              49.908787000867136\n" +
            "            ],\n" +
            "            [\n" +
            "              39.94628906249999,\n" +
            "              49.908787000867136\n" +
            "            ],\n" +
            "            [\n" +
            "              39.94628906249999,\n" +
            "              46.195042108660154\n" +
            "            ]";
}
