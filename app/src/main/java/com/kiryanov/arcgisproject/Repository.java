package com.kiryanov.arcgisproject;

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

    @FunctionalInterface
    interface AddMarker {
        void addMarker(double lat, double lng);
    }

    @FunctionalInterface
    public interface Function {
        void execute();
    }

    private double markerOffset = 0;
    public void addPoints(int count, AddMarker addMarker, Function setButtonText) {
        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);

            double x = LAT + dispersionX + markerOffset;
            double y = LNG + dispersionY + markerOffset;

            addMarker.addMarker(x, y);
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
