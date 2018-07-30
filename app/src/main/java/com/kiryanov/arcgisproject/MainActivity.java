package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private GraphicsOverlay overlay;

    private Button button;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map_view);
        initMapView();

        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.loading);

        button.setOnClickListener(v -> initGeoJson());
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

    private Disposable disposable;
    private void initGeoJson() {
        if (disposable == null || disposable.isDisposed()) {
            Repository.getInstance().getDistricts(this)
                    .subscribe(new Observer<Polygon>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNext(Polygon polygon) {
                            overlay.getGraphics().add(new Graphic(
                                    polygon,
                                    new SimpleFillSymbol(
                                            SimpleFillSymbol.Style.SOLID,
                                            Color.BLUE,
                                            new SimpleLineSymbol(
                                                    SimpleLineSymbol.Style.SOLID,
                                                    Color.BLACK, 3
                                            )
                                    )
                            ));
                        }

                        @Override
                        public void onError(Throwable e) {
                            disposable.dispose();
                            showMessage(e.getMessage());
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onComplete() {
                            disposable.dispose();
                            progressBar.setVisibility(View.GONE);
                        }
                    });

        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*private void addPolygons() {
        int count = 100;

        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.Style.CROSS, Color.BLUE, null
        );

        PointCollection points = new PointCollection(SpatialReferences.getWgs84());

        points.add(39.6, 47.2);
        for (int i = 0; i < count; i++) {
            double offset = ((double) i) / count;
            points.add(39.6 + offset, 47.2 + offset);
        }
        points.add(39.6, 47.2);

        Polygon polygon = new Polygon(points);
        overlay.getGraphics().add(new Graphic(polygon, fillSymbol));

        polygonOffset += 0.2;

        setButtonText(addPolygonBtn, count);

        if (polygonOffset > 80) polygonOffset = -80;
    }*/

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
}
