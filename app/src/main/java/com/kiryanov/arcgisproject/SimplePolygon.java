package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.support.annotation.NonNull;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.PointReducer;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeniy on 30.07.18.
 */

public class SimplePolygon extends Polygon{

    private List<GeoPoint> originalPoints;
    private List<GeoPoint> simplePoints;

    public SimplePolygon(@NonNull List<GeoPoint> originalPoints, double tolerance) {
        this.originalPoints = new ArrayList<>(originalPoints);
        simplePoints = PointReducer.reduceWithTolerance(
                ((ArrayList<GeoPoint>) this.originalPoints), tolerance);

        setFillColor(Color.GRAY);
        setStrokeWidth(1f);
        setPoints(simplePoints);
    }

    public void showOriginal() {
        setPoints(originalPoints);
    }

    public void showSimple() {
        setPoints(simplePoints);
    }
}
