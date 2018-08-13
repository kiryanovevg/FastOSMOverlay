package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private Button btnPoints;
    private Button btnPolygons;
    private ProgressBar progressBar;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey(getString(R.string.api_key));
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);

        initMapView();

        progressBar = findViewById(R.id.progress_bar);

        btnPoints = findViewById(R.id.btn_points);
        btnPolygons = findViewById(R.id.btn_polygons);

        btnPoints.setOnClickListener(v -> addMarkers());
        btnPolygons.setOnClickListener(v -> addPolygons());
    }

    private void initMapView() {
        mapView = findViewById(R.id.map_view);
        mapView.getMap().move(new CameraPosition(
                new Point(LAT, LNG),
                10f, 0f, 0f
        ));
    }

    private Disposable disposable;

    private void addPolygons() {
        if (disposable == null || disposable.isDisposed()) {
//            Repository.getInstance().getDistricts(this)
            Repository.getInstance().getCustomPolygon(this, 600)
                    .subscribe(new Observer<List<Polygon>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNext(List<Polygon> polygons) {
                            for (Polygon polygon : polygons) {
                                mapView.getMap().getMapObjects().addPolygon(polygon);
                            }
                            setButtonText(btnPolygons, polygons.size());
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

    private double markerOffset = 0;

    private void addMarkers() {
        int count = 1000;

        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);


            mapView.getMap().getMapObjects().addCollection().addPlacemark(
                    new Point(LAT + markerOffset + dispersionY, LNG + dispersionX)
            );
        }

        if (markerOffset > 80) markerOffset = -80;
        markerOffset += density;
        setButtonText(btnPoints, count);
    }

    private double getDispersion(int random, int accuracy, double density) {
        return (((double) random * random) / accuracy) % density;
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
    protected void onStart() {
        MapKitFactory.getInstance().onStart();
        mapView.onStart();

        super.onStart();
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        mapView.onStop();

        super.onStop();
    }
}
