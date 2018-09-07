package com.kiryanov.arcgisproject.Overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

/**
 * Created by Evgeniy on 07.09.18.
 */

public class CustomOverlay extends Overlay {

    private DrawingAlgorithm drawingAlgorithm;

    public CustomOverlay() {
//        drawingAlgorithm = new PointsDrawing(10);
        drawingAlgorithm = new ClustersDrawing();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, final MapView mapView) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingAlgorithm.startBoundingBox = mapView.getBoundingBox();
                drawingAlgorithm.startProjection = mapView.getProjection();
                break;

            case MotionEvent.ACTION_MOVE:
                drawingAlgorithm.hasMoved = true;
                break;

            case MotionEvent.ACTION_UP:
                drawingAlgorithm.hasMoved = false;
                drawingAlgorithm.startBoundingBox = mapView.getBoundingBox();
                drawingAlgorithm.startProjection = mapView.getProjection();

                mapView.invalidate();

                break;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) return;

        drawingAlgorithm.draw(canvas, mapView);
    }

    public void add(IGeoPoint point) {
        drawingAlgorithm.add(point);
    }

    public void addAll(List<IGeoPoint> pointList) {
        drawingAlgorithm.addAll(pointList);
    }

    public void clear() {
        drawingAlgorithm.clear();
    }

    @Override
    public BoundingBox getBounds() {
        return drawingAlgorithm.getBounds();
    }

    public void setIcon(Bitmap icon) {
        drawingAlgorithm.setIcon(icon);
    }
}
