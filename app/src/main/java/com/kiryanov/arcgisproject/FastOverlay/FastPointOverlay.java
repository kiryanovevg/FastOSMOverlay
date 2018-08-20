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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastPointOverlay extends Overlay {

    private List<IGeoPoint> pointList;
    private List<Point> gridIndex;

    private int gridWid, gridHei, viewWid, viewHei;
    private boolean gridBool[][];
    private boolean hasMoved = false;
    private BoundingBox boundingBox;
    private BoundingBox startBoundingBox;
    private Projection startProjection;

    private Bitmap icon;
    private int cellSize = 10;

    public FastPointOverlay() {
        pointList = new ArrayList<>();

        this.boundingBox = findBoundingBox();

        cellSize = icon == null
                ? 10
                : icon.getWidth() > icon.getHeight()
                    ? icon.getWidth() / 2
                    : icon.getHeight() / 2;
    }

    private void updateGrid(MapView mapView) {
        viewWid = mapView.getWidth();
        viewHei = mapView.getHeight();
        gridWid = (int) Math.floor((float) viewWid / cellSize) + 1;
        gridHei = (int) Math.floor((float) viewHei / cellSize) + 1;
        gridBool = new boolean[gridWid][gridHei];
    }

    private void computeGrid(final MapView pMapView) {
        BoundingBox viewBBox = pMapView.getBoundingBox();

        startBoundingBox = viewBBox;
        startProjection = pMapView.getProjection();

        if (gridBool == null || viewHei != pMapView.getHeight() || viewWid != pMapView.getWidth()) {
            updateGrid(pMapView);
        } else {
            for (boolean[] row : gridBool)
                Arrays.fill(row, false);
        }

        int gridX, gridY;
        final Point mPositionPixels = new Point();
        gridIndex = new ArrayList<>();

        for (IGeoPoint pt1 : pointList) {
            if (pt1 == null) continue;
            if (pt1.getLatitude() > viewBBox.getLatSouth()
                    && pt1.getLatitude() < viewBBox.getLatNorth()
                    && pt1.getLongitude() > viewBBox.getLonWest()
                    && pt1.getLongitude() < viewBBox.getLonEast()) {

                Utils.coordinateToPixels(viewWid, viewHei, viewBBox, pt1, mPositionPixels);

                // test whether in this grid cell there is already a point, skip if yes
                gridX = (int) Math.floor((float) mPositionPixels.x / cellSize);
                gridY = (int) Math.floor((float) mPositionPixels.y / cellSize);
                if (gridX >= gridWid || gridY >= gridHei || gridX < 0 || gridY < 0
                        || gridBool[gridX][gridY])
                    continue;
                gridBool[gridX][gridY] = true;
                gridIndex.add(new Point(mPositionPixels));
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, final MapView mapView) {
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
    public void draw(Canvas canvas, MapView mapView, boolean b) {
        if (b) return;
        final Projection pj = mapView.getProjection();

        // optimized for speed, recommended for > 10k points
        // recompute grid only on specific events - only onDraw but when not animating
        // and not in the middle of a touch scroll gesture

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
        for (Point point: gridIndex) {
            tx = (point.x * dd.x) / pStartSe.x;
            ty = (point.y * dd.y) / pStartSe.y;

            drawPointAt(canvas, point.x + pNw.x + tx, point.y + pNw.y + ty);
        }
    }

    protected void drawPointAt(Canvas canvas, float x, float y) {
        if (icon != null) {
            canvas.drawBitmap(icon, x, y, null);
        } else {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);

            canvas.drawRect(
                    x - cellSize/2,
                    y - cellSize/2,
                    x + cellSize/2,
                    y + cellSize/2,
                    paint
            );
        }
    }

    private BoundingBox findBoundingBox() {
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

    public void add(IGeoPoint point) {
        pointList.add(point);
    }

    public void addAll(List<IGeoPoint> pointList) {
        this.pointList.addAll(pointList);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}