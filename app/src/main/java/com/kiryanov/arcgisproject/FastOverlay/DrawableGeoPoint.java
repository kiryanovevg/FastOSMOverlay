package com.kiryanov.arcgisproject.FastOverlay;

import android.graphics.drawable.Drawable;

import org.osmdroid.api.IGeoPoint;

/**
 * Created by Evgeniy on 13.08.18.
 */

public class DrawableGeoPoint implements IGeoPoint {

    private Drawable drawable;
    private double latitude;
    private double longitude;

    public DrawableGeoPoint(Drawable drawable, double latitude, double longitude) {
        this.drawable = drawable;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int getLatitudeE6() {
        return ((int) latitude);
    }

    @Override
    public int getLongitudeE6() {
        return ((int) longitude);
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
