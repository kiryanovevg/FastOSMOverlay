package com.kiryanov.arcgisproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final double LAT = 47.2;
    private static final double LNG = 39.7;

    private Button button;
//    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView();

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {});
    }

    private void initMapView() {
        /*MapKitFactory.setApiKey(getString(R.string.api_key));
        MapKitFactory.initialize(this);
        mapView = findViewById(R.id.mapview);

        mapView.getMap().move(new CameraPosition(
                new Point(LAT, LNG),
                10f, 0f, 0f
        ));*/
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
//        MapKitFactory.getInstance().onStart();
//        mapView.onStart();

        super.onStart();
    }

    @Override
    protected void onStop() {
//        MapKitFactory.getInstance().onStop();
//        mapView.onStop();

        super.onStop();
    }
}
