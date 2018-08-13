package com.kiryanov.arcgisproject;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Random;

/**
 * Created by Evgeniy on 13.08.18.
 */

public class Repository {

    public static final double LAT = 47.2;
    public static final double LNG = 39.7;

    private static Repository instance;

    private Repository() {}

    public static Repository getInstance() {
        if (instance == null)
            instance = new Repository();

        return instance;
    }

    private double markerOffset = 0;
    public void addPoints(int count, MapView mapView, Function setButtonText) {
        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);

            Marker marker = new Marker(mapView);
            marker.setIcon(
                    mapView.getContext().getResources().getDrawable(R.drawable.moreinfo_arrow)
            );
            marker.setPosition(new GeoPoint(
                    LAT + dispersionX + markerOffset, LNG + dispersionY
            ));

            mapView.getOverlays().add(marker);
        }

        if (markerOffset > 80) markerOffset = -80;
        markerOffset += density;

        setButtonText.execute();
    }

    //X, Y
    private double getDispersion(int random, int accuracy, double density) {
        return (((double) random * random) / accuracy) % density;
    }
}
