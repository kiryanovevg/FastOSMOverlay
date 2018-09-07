package com.kiryanov.arcgisproject.Overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import java.util.List;

/**
 * Created by Evgeniy on 07.09.18.
 */

public abstract class DrawingAlgorithm {

    protected int gridWid, gridHei, viewWid, viewHei;
    protected boolean hasMoved = false;
    protected BoundingBox startBoundingBox;
    protected Projection startProjection;

    protected Bitmap icon;

    protected abstract void draw(Canvas canvas, MapView mapView);
    protected abstract void updateGrid(MapView mapView);

    protected abstract void add(IGeoPoint point);
    protected abstract void addAll(List<IGeoPoint> pointList);
    protected abstract void clear();
    protected abstract List<IGeoPoint> getItems();

    protected abstract int getCellSize();


    protected void drawPointAt(Canvas canvas, float x, float y, int count) {
        int cellSize = getCellSize();

        if (icon != null) {
            canvas.drawBitmap(
                    icon,
                    x - cellSize/2,
                    y - cellSize/2,
                    null
            );
        } else {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);

            canvas.drawCircle(
                    x - cellSize/2,
                    y - cellSize/2,
                    cellSize / 2,
                    paint
            );
        }

        if (count != 1) {
            Paint textPaint = new Paint();
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(20f);

            canvas.drawText(String.valueOf(count), x, y, textPaint);
        }
    }

    private BoundingBox findBoundingBox(List<IGeoPoint> pointList) {
        Double east = null, west = null, north = null, south = null;
        for(IGeoPoint p : pointList) {
            if(p == null) continue;
            if(east == null || p.getLongitude() > east) east = p.getLongitude();
            if(west == null || p.getLongitude() < west) west = p.getLongitude();
            if(north == null || p.getLatitude() > north) north = p.getLatitude();
            if(south == null || p.getLatitude() < south) south = p.getLatitude();
        }

        if(east != null)
            return new BoundingBox(north, east, south, west);
        else
            return null;
    }

    public BoundingBox getBounds() {
        return findBoundingBox(getItems());
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
