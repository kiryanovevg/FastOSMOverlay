package com.kiryanov.arcgisproject.FastOverlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Evgeniy on 20.08.18.
 */

public class FastPointOverlay extends Overlay {

    protected List<IGeoPoint> pointList;
    protected Drawable drawable;
    protected int viewWid, viewHei;
    protected int gridWid, gridHei;
    protected boolean[][] gridBool;

    protected boolean hasMoved;

    protected int mCellSize = 10;

    public FastPointOverlay(MapView mapView, Drawable drawable) {
        pointList = new ArrayList<>();

        this.drawable = drawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                startBoundingBox = mapView.getBoundingBox();
//                startProjection = mapView.getProjection();
                break;

            case MotionEvent.ACTION_MOVE:
                hasMoved = true;
                break;

            case MotionEvent.ACTION_UP:
                hasMoved = false;
//                startBoundingBox = mapView.getBoundingBox();
//                startProjection = mapView.getProjection();
                mapView.invalidate();
                break;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        final BoundingBox viewBBox = mapView.getBoundingBox();
        final Point mPositionPixels = new Point();

        if (!hasMoved && !mapView.isAnimating()) {
            if (gridBool == null || viewHei != mapView.getHeight() ||
                    viewWid != mapView.getWidth())
                updateGrid(mapView);
            else
                for (boolean[] row : gridBool)
                    Arrays.fill(row, false);
        }

        int gridX, gridY;
        for (IGeoPoint point : pointList) {
            if (point == null) continue;
            if (point.getLatitude() > viewBBox.getLatSouth()
                    && point.getLatitude() < viewBBox.getLatNorth()
                    && point.getLongitude() > viewBBox.getLonWest()
                    && point.getLongitude() < viewBBox.getLonEast()) {

                Utils.coordinateToPixels(viewWid, viewHei, viewBBox, point, mPositionPixels);

                gridX = (int) Math.floor((float) mPositionPixels.x / mCellSize);
                gridY = (int) Math.floor((float) mPositionPixels.y / mCellSize);
                if (gridX >= gridWid || gridY >= gridHei || gridX < 0 || gridY < 0
                        || gridBool[gridX][gridY])
                    continue;
                gridBool[gridX][gridY] = true;

                drawPointAt(canvas, mPositionPixels.x, mPositionPixels.y);
            }
        }
    }

    protected void drawPointAt(Canvas canvas, float x, float y) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        canvas.drawBitmap(
                bitmap,
                x - bitmap.getWidth() / 2,
                y - bitmap.getHeight() / 2,
                null
        );
    }

    private void updateGrid(MapView mapView) {
        viewWid = mapView.getWidth();
        viewHei = mapView.getHeight();
        gridWid = (int) Math.floor((float) viewWid / mCellSize) + 1;
        gridHei = (int) Math.floor((float) viewHei / mCellSize) + 1;
        gridBool = new boolean[gridWid][gridHei];

        // TODO the measures on first draw are not the final values.
        // MapView should propagate onLayout to overlays
    }

    public void add(GeoPoint point) {
        pointList.add(point);
    }

    public void addAll(List<IGeoPoint> pointList) {
        this.pointList.addAll(pointList);
    }
}
