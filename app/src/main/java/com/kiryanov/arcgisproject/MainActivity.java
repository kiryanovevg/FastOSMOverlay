package com.kiryanov.arcgisproject;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kiryanov.arcgisproject.FastOverlay.FastPointOverlay;
import com.kiryanov.arcgisproject.Overlay.CustomOverlay;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.kiryanov.arcgisproject.Repository.LAT;
import static com.kiryanov.arcgisproject.Repository.LNG;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private MapView mapView;
    private ProgressBar progressBar;

    private Button btnPoints;
    private Button btnPolygons;
    private Button btnInvalidate;
    private Button btnNext;

//    private List<IGeoPoint> geoPoints = new ArrayList<>();
//    private FastPointOverlay fastPointOverlay;
//    private FastPointCluster fastPointCluster;
    private CustomOverlay customOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());

        progressBar = findViewById(R.id.progress_bar);
        btnPoints = findViewById(R.id.btn_points);
        btnPolygons = findViewById(R.id.btn_polygons);
        btnInvalidate = findViewById(R.id.btn_invalidate);
        btnNext = findViewById(R.id.btn_next);

        btnPoints.setOnClickListener(v -> addPoints());
        btnPolygons.setOnClickListener(v -> addPointsFromGeoJson());
        btnInvalidate.setOnClickListener(v -> invalidateMap());
        btnNext.setOnClickListener(v -> customOverlay.setIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.direction_arrow)).getBitmap()));

        initMapView(savedInstanceState);
    }

    private void initMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(8d);
        mapView.getController().setCenter(new GeoPoint(LAT, LNG));

        ///***///

//        fastPointOverlay = new FastPointOverlay(mapView);
//        fastPointOverlay.setIcon(
//                ((BitmapDrawable) getResources().getDrawable(R.drawable.direction_arrow)).getBitmap()
//        );

//        fastPointCluster = new FastPointCluster(mapView);

        customOverlay = new CustomOverlay();

        ///***///

        mapView.getOverlays().add(customOverlay);
//        mapView.getOverlays().add(fastPointOverlay);
//        mapView.getOverlays().add(fastPointCluster);
    }

    private void addPoints() {
        int count = 10000;

        Repository.getInstance().addPoints(
                count,
                this::addMarker,
                () -> setButtonText(btnPoints, count)
        );
    }

    private void addMarker(double lat, double lng) {
        /*geoPoints.add(new DrawableGeoPoint(
                getResources().getDrawable(R.drawable.moreinfo_arrow),
                lat, lng
        ));*/

        customOverlay.add(new GeoPoint(lat, lng));
//        fastPointOverlay.add(new GeoPoint(lat, lng));
//        fastPointCluster.add(new GeoPoint(lat, lng));
    }

    private void addPointsFromGeoJson() {
        Repository.getInstance().getPointsFromGeoJson(this)
                .subscribe(new Observer<List<IGeoPoint>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<IGeoPoint> pointList) {
                        customOverlay.addAll(pointList);
//                        fastPointOverlay.addAll(pointList);
//                        fastPointCluster.addAll(pointList);
                        setButtonText(btnPolygons, pointList.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        showMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBar.setVisibility(View.GONE);
                        mapView.invalidate();
                    }
                });
    }

    private void invalidateMap() {
//        mapView.getOverlays().clear();

        mapView.invalidate();

//        btnPoints.setText("0");
//        btnPolygons.setText("0");
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