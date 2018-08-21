package com.kiryanov.arcgisproject.FastOverlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FastPointCluster extends Overlay {

    private List<IGeoPoint> pointList;
    private List<IGeoPoint> cloneList;
    private List<Point> gridIndex;
    private List<List<IGeoPoint>> clusters;

    private int gridWid, gridHei, viewWid, viewHei;
    private boolean gridBool[][];
    private boolean hasMoved = false;
    private BoundingBox boundingBox;
    private BoundingBox startBoundingBox;
    private Projection startProjection;

    private Bitmap icon;
    private int cellSize = 10;

    private int zoomLevel;

    public FastPointCluster(MapView mapView) {
        pointList = new ArrayList<>();
        cloneList = new ArrayList<>();
        clusters = new ArrayList<>();

        this.boundingBox = findBoundingBox();

        cellSize = icon == null
                ? 10
                : icon.getWidth() > icon.getHeight()
                    ? icon.getWidth() / 2
                    : icon.getHeight() / 2;
    }

    @Override
    public void onDetach(MapView mapView) {
        super.onDetach(mapView);
    }

    @Override
    public boolean onFling(MotionEvent pEvent1, MotionEvent pEvent2,
                           float pVelocityX, float pVelocityY, MapView pMapView) {

        return super.onFling(pEvent1, pEvent2, pVelocityX, pVelocityY, pMapView);
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

                /*final int currentZoomLevel = ((int) mapView.getZoomLevelDouble());
                if (zoomLevel != currentZoomLevel) {
                    zoomLevel = currentZoomLevel;
                    computeCluster(mapView);
                }*/

                mapView.invalidate();

                break;
        }
        return false;
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
    public void draw(Canvas canvas, MapView mapView, boolean b) {
        if (b) return;
        final Projection pj = mapView.getProjection();

        // optimized for speed, recommended for > 10k points
        // recompute grid only on specific events - only onDraw but when not animating
        // and not in the middle of a touch scroll gesture

        if (gridBool == null) {
            updateGrid(mapView);
        }

        final int currentZoomLevel = ((int) mapView.getZoomLevelDouble());
        if (zoomLevel == 0 || zoomLevel != currentZoomLevel) {
            zoomLevel = currentZoomLevel;
            computeCluster(mapView);
        }

//        if (gridBool == null || (!hasMoved && !mapView.isAnimating()))
//            computeGrid(mapView);

        // compute the coordinates of each visible point in the new viewbox
        IGeoPoint nw = new GeoPoint(startBoundingBox.getLatNorth(), startBoundingBox.getLonWest());
        IGeoPoint se = new GeoPoint(startBoundingBox.getLatSouth(), startBoundingBox.getLonEast());
        Point pNw = pj.toPixels(nw, null);
        Point pSe = pj.toPixels(se, null);
        Point pStartSe = startProjection.toPixels(se, null);
        Point dGz = new Point(pSe.x - pStartSe.x, pSe.y - pStartSe.y);
        Point dd = new Point(dGz.x - pNw.x, dGz.y - pNw.y);
        float tx, ty;

        final Point point = new Point();
        // draw points

        if (clusters.size() != 1) {
            for (List<IGeoPoint> cluster : clusters) {
                IGeoPoint pt1 = cluster.get(0);
                if (pt1.getLatitude() > mapView.getBoundingBox().getLatSouth()
                        && pt1.getLatitude() < mapView.getBoundingBox().getLatNorth()
                        && pt1.getLongitude() > mapView.getBoundingBox().getLonWest()
                        && pt1.getLongitude() < mapView.getBoundingBox().getLonEast()) {

                    Utils.coordinateToPixels(viewWid, viewHei, startBoundingBox, pt1, point);
                    tx = (point.x * dd.x) / pStartSe.x;
                    ty = (point.y * dd.y) / pStartSe.y;

                    drawPointAt(canvas, point.x + pNw.x + tx, point.y + pNw.y + ty, cluster.size());
                }
            }
        } else {
            for (IGeoPoint pt1 : clusters.get(0)) {
                if (pt1.getLatitude() > mapView.getBoundingBox().getLatSouth()
                        && pt1.getLatitude() < mapView.getBoundingBox().getLatNorth()
                        && pt1.getLongitude() > mapView.getBoundingBox().getLonWest()
                        && pt1.getLongitude() < mapView.getBoundingBox().getLonEast()) {

                    Utils.coordinateToPixels(viewWid, viewHei, startBoundingBox, pt1, point);
                    tx = (point.x * dd.x) / pStartSe.x;
                    ty = (point.y * dd.y) / pStartSe.y;

                    drawPointAt(canvas, point.x + pNw.x + tx, point.y + pNw.y + ty, 1);
                }
            }
        }
    }

    protected int maxClusteringZoomLevel = 17;
    protected int minClusteringZoomLevel = 6;
    protected int mRadiusInPixels = 100;
    protected double mRadiusInMeters;

    private Thread thread;
    private List<List<IGeoPoint>> threadList = new ArrayList<>();

    private void computeCluster(MapView mapView) {
        Toast.makeText(mapView.getContext(), "Start clustering", Toast.LENGTH_SHORT).show();

        if (thread == null) {
            thread = new Thread(() -> {
                threadList.clear();
                cloneList.clear();
                cloneList.addAll(pointList);

                convertRadiusToMeters(mapView);

                while (!cloneList.isEmpty()) {
                    IGeoPoint center = cloneList.get(0);
                    List<IGeoPoint> cluster = createCluster(center, mapView);
                    threadList.add(cluster);
                }

                clusters.clear();
                clusters.addAll(threadList);

                thread = null;
                mapView.postInvalidate();
            });
            thread.start();
        }
    }

    private List<IGeoPoint> createCluster(IGeoPoint center, MapView mapView) {
        GeoPoint clusterPosition = new GeoPoint(center.getLatitude(), center.getLongitude());

        List<IGeoPoint> cluster = new ArrayList<>();
        cluster.add(center);
        cloneList.remove(center);

        /*if (mapView.getZoomLevelDouble() > maxClusteringZoomLevel) {
            //above max level => block clustering:
            return cluster;
        }*/

        if (mapView.getZoomLevelDouble() < minClusteringZoomLevel) {
            cluster.addAll(cloneList);
            cloneList.clear();
            return cluster;
        }

        Iterator<IGeoPoint> it = cloneList.iterator();
        while (it.hasNext()) {
            IGeoPoint neighbour = it.next();
            double distance = clusterPosition.distanceToAsDouble(neighbour);
            if (distance <= mRadiusInMeters) {
                cluster.add(neighbour);
                it.remove();
            }
        }

        return cluster;
    }

    private void convertRadiusToMeters(MapView mapView) {

        Rect mScreenRect = mapView.getIntrinsicScreenRect(null);

        int screenWidth = mScreenRect.right - mScreenRect.left;
        int screenHeight = mScreenRect.bottom - mScreenRect.top;

        BoundingBox bb = mapView.getBoundingBox();

        double diagonalInMeters = bb.getDiagonalLengthInMeters();
        double diagonalInPixels = Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);
        double metersInPixel = diagonalInMeters / diagonalInPixels;

        mRadiusInMeters = mRadiusInPixels * metersInPixel;
    }

    protected void drawPointAt(Canvas canvas, float x, float y, int count) {
        /*if (icon != null) {
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
        }*/

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        int size = 20;

        canvas.drawCircle(
                x - size/2,
                y - size/2,
                size / 2,
                paint
        );

        if (count != 1) {
            Paint textPaint = new Paint();
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(20f);

            canvas.drawText(String.valueOf(count), x, y, textPaint);
        }

    }

    private BoundingBox findBoundingBox() {
        Double east = null, west = null, north = null, south = null;
        for (IGeoPoint p : pointList) {
            if(p == null) continue;
            if(east == null || p.getLongitude() > east) east = p.getLongitude();
            if(west == null || p.getLongitude() < west) west = p.getLongitude();
            if(north == null || p.getLatitude() > north) north = p.getLatitude();
            if(south == null || p.getLatitude() < south) south = p.getLatitude();
        }

        if (east != null)
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

    @Override
    public BoundingBox getBounds() {
        return boundingBox;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}