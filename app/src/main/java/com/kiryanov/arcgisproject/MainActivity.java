package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView();

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {});
    }

    private void initMapView() {
        MapKitFactory.setApiKey(getString(R.string.api_key));
        MapKitFactory.initialize(this);
        mapView = findViewById(R.id.mapview);

        mapView.getMap().move(
                new CameraPosition(
                        new Point()
                )
        );
    }

    private void buttonClick() {

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
