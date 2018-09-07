package com.kiryanov.arcgisproject.Overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Icon;

import com.kiryanov.arcgisproject.FastOverlay.Utils;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Evgeniy on 07.09.18.
 */

public class PointsDrawing extends DrawingAlgorithm {

    private List<IGeoPoint> pointList = new ArrayList<>();
    private List<Point> gridIndex;
    private boolean gridBool[][];

    private int cellSize;

    public PointsDrawing(Bitmap icon, int cellSize) {
        this.icon = icon;
        this.cellSize = cellSize;
    }

    public PointsDrawing(int cellSize) {
        this(null, cellSize);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView) {
        final Projection pj = mapView.getProjection();

        // optimized for speed, recommended for > 10k points
        // recompute grid only on specific events - only onDraw but when not animating
        // and not in the middle of a touch scroll gesture

//        if (gridBool == null || (!hasFling && !hasMoved && !mapView.isAnimating()))
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

            super.drawPointAt(canvas, point.x + pNw.x + tx, point.y + pNw.y + ty, 1);
        }
    }

    private void computeGrid(final MapView mapView) {
        BoundingBox viewBBox = mapView.getBoundingBox();

        startBoundingBox = viewBBox;
        startProjection = mapView.getProjection();

        if (gridBool == null || viewHei != mapView.getHeight() || viewWid != mapView.getWidth()) {
            updateGrid(mapView);
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
    protected void updateGrid(MapView mapView) {
        viewWid = mapView.getWidth();
        viewHei = mapView.getHeight();
        gridWid = (int) Math.floor((float) viewWid / cellSize) + 1;
        gridHei = (int) Math.floor((float) viewHei / cellSize) + 1;
        gridBool = new boolean[gridWid][gridHei];
    }

    @Override
    protected void add(IGeoPoint point) {
        pointList.add(point);
    }

    @Override
    protected void addAll(List<IGeoPoint> pointList) {
        this.pointList.addAll(pointList);
    }

    @Override
    protected void clear() {
        pointList.clear();
    }

    @Override
    protected List<IGeoPoint> getItems() {
        return pointList;
    }

    @Override
    protected int getCellSize() {
        return cellSize;
    }
}
