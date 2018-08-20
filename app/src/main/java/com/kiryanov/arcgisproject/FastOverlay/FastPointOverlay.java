package com.kiryanov.arcgisproject.FastOverlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Evgeniy on 20.08.18.
 */

public class FastPointOverlay extends Overlay {

    protected List<IGeoPoint> pointList;
    protected List<Point> gridIndex;
    protected boolean[][] gridBool;

    protected Bitmap icon;
    protected boolean hasMoved;
    protected int viewWid, viewHei;
    protected int gridWid, gridHei;

    protected BoundingBox startBoundingBox;
    protected Projection startProjection;

    protected int mCellSize = 10;

    public FastPointOverlay(MapView mapView, Bitmap icon) {
        pointList = new ArrayList<>();

        this.icon = icon;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startBoundingBox = mapView.getBoundingBox();
                startProjection = mapView.getProjection();
                break;

            case MotionEvent.ACTION_MOVE:
                hasMoved = true;
                break;

            case MotionEvent.ACTION_UP:
                hasMoved = false;
                startBoundingBox = mapView.getBoundingBox();
                startProjection = mapView.getProjection();
                mapView.invalidate();
                break;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        final BoundingBox viewBBox = mapView.getBoundingBox();
        final Point mPositionPixels = new Point();
        final Projection pj = mapView.getProjection();

        if (gridBool == null || (!hasMoved && !mapView.isAnimating()))
            computeGrid(mapView);

        // compute the coordinates of each visible point in the new viewbox
        IGeoPoint nw = new GeoPoint(startBoundingBox.getLatNorth(), startBoundingBox.getLonWest());
        IGeoPoint se = new GeoPoint(startBoundingBox.getLatSouth(), startBoundingBox.getLonEast());
        Point pNw = pj.toPixels(nw, null);
        Point pSe = pj.toPixels(se, null);
        Point pStartSe = startProjection.toPixels(se, null);
        Point dGz = new Point(pSe.x - pStartSe.x, pSe.y - pStartSe.y);
        Point dd = new Point(dGz.x - pNw.x, dGz.y - pNw.y);
        float tx, ty;
        
        // draw points
        for (Point point : gridIndex) {
            tx = (point.x * dd.x) / pStartSe.x;
            ty = (point.y * dd.y) / pStartSe.y;

            drawPointAt(canvas, point.x + pNw.x + tx, point.y + pNw.y + ty);
        }
    }

    protected void drawPointAt(Canvas canvas, float x, float y) {
        if (icon != null) {
            canvas.drawBitmap(
                    icon,
                    x - icon.getWidth() / 2,
                    y - icon.getHeight() / 2,
                    null
            );
        } else {
            Paint paint = new Paint();
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#ffff00"));

            canvas.drawRect(
                    x, y, x + 10, y + 10, paint
            );
        }
    }

    private void computeGrid(final MapView pMapView) {
        final BoundingBox viewBBox = pMapView.getBoundingBox();
        startBoundingBox = viewBBox;
        startProjection = pMapView.getProjection();

        if (viewHei != pMapView.getHeight() || viewWid != pMapView.getWidth()) {
            updateGrid(pMapView);
        }

        int gridX, gridY;
        final Point mPositionPixels = new Point();
        final Projection pj = pMapView.getProjection();
        gridIndex = new ArrayList<>();

        for (IGeoPoint pt1 : pointList) {
            if (pt1 == null) continue;
            if (pt1.getLatitude() > viewBBox.getLatSouth()
                    && pt1.getLatitude() < viewBBox.getLatNorth()
                    && pt1.getLongitude() > viewBBox.getLonWest()
                    && pt1.getLongitude() < viewBBox.getLonEast()) {
                Utils.coordinateToPixels(viewWid, viewHei ,viewBBox, pt1, mPositionPixels);
                // test whether in this grid cell there is already a point, skip if yes
                gridX = (int) Math.floor((float) mPositionPixels.x / mCellSize);
                gridY = (int) Math.floor((float) mPositionPixels.y / mCellSize);
                if (gridX >= gridWid || gridY >= gridHei || gridX < 0 || gridY < 0
                        || gridBool[gridX][gridY])
                    continue;
                gridBool[gridX][gridY] = true;
                gridIndex.add(new Point(mPositionPixels));
            }
        }
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
