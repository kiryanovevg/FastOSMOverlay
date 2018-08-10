package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

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

    private Button button;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey(getString(R.string.api_key));
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);

        initMapView();

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> buttonClick());
    }

    private void initMapView() {
        mapView = findViewById(R.id.map_view);
        mapView.getMap().move(new CameraPosition(
                new Point(LAT, LNG),
                10f, 0f, 0f
        ));
    }

    private void buttonClick() {
        addMarkers();
        addPolygons();
    }

    private void addPolygons() {
        Repository.getInstance().getCustomPolygon(this)
                .subscribe(new Observer<List<Polygon>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Polygon> polygons) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
        button.setText(String.valueOf(Integer.parseInt(button.getText().toString()) + count));
    }

    private double getDispersion(int random, int accuracy, double density) {
        return (((double) random * random) / accuracy) % density;
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
