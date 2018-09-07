package com.kiryanov.arcgisproject.FastOverlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import com.kiryanov.arcgisproject.Clustering.ClusteringAlgorithm;
import com.kiryanov.arcgisproject.Clustering.StaticCluster;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FastPointCluster extends Overlay {
    
    private ClusteringAlgorithm<IGeoPoint> algorithm;
    private Set clusters;

    private int zoomLevel;
    private int gridWid, gridHei, viewWid, viewHei;
    private boolean hasMoved = false;
    private BoundingBox boundingBox;
    private BoundingBox startBoundingBox;
    private Projection startProjection;

    private Bitmap icon;
    private int cellSize = 10;

    public FastPointCluster(MapView mapView) {
        algorithm = new ClusteringAlgorithm<>();
        boundingBox = findBoundingBox();

        cellSize = icon == null
                ? 10
                : icon.getWidth() > icon.getHeight()
                ? icon.getWidth() / 2
                : icon.getHeight() / 2;
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

    private void updateGrid(MapView mapView) {
        viewWid = mapView.getWidth();
        viewHei = mapView.getHeight();
        gridWid = (int) Math.floor((float) viewWid / cellSize) + 1;
        gridHei = (int) Math.floor((float) viewHei / cellSize) + 1;
    }

//    private Thread thread;
    private Disposable disposable;

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean b) {
        if (b) return;

//        if (algorithm.getItems().isEmpty()) return;

        final Projection pj = mapView.getProjection();
        final BoundingBox viewBBox = mapView.getBoundingBox();

        startBoundingBox = viewBBox;
        startProjection = mapView.getProjection();

        int currentZoomLevel = ((int) mapView.getZoomLevelDouble());
        if (zoomLevel != currentZoomLevel) {
            updateGrid(mapView);
            zoomLevel = currentZoomLevel;

            /*if (thread != null && !thread.isInterrupted()) {
                thread.interrupt();
            }

            thread = new Thread(() -> {
                clusters = algorithm.getClusters(mapView.getZoomLevelDouble());
                mapView.postInvalidate();
                thread = null;
            });

            thread.start();*/

            Single.fromCallable(() -> algorithm.getClusters(mapView.getZoomLevelDouble()))
                    .doOnSubscribe(d -> {
                        if (disposable != null && !disposable.isDisposed())
                            disposable.dispose();

                        disposable = d;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(clusters -> {
                        FastPointCluster.this.clusters = clusters;
                        mapView.invalidate();
                    })
                    .subscribeOn(Schedulers.computation())
                    .subscribe();
        }
        
        IGeoPoint nw = new GeoPoint(startBoundingBox.getLatNorth(), startBoundingBox.getLonWest());
        IGeoPoint se = new GeoPoint(startBoundingBox.getLatSouth(), startBoundingBox.getLonEast());
        Point pNw = pj.toPixels(nw, null);
        Point pSe = pj.toPixels(se, null);
        Point pStartSe = startProjection.toPixels(se, null);
        Point dGz = new Point(pSe.x - pStartSe.x, pSe.y - pStartSe.y);
        Point dd = new Point(dGz.x - pNw.x, dGz.y - pNw.y);
        float tx, ty;

        final Point point = new Point();
        for (Object object : clusters) {
            if (object instanceof StaticCluster) {
                StaticCluster cluster = ((StaticCluster) object);
                GeoPoint geoPoint = ((GeoPoint) cluster.getPosition());
                
                if (geoPoint.getLatitude() > viewBBox.getLatSouth()
                        && geoPoint.getLatitude() < viewBBox.getLatNorth()
                        && geoPoint.getLongitude() > viewBBox.getLonWest()
                        && geoPoint.getLongitude() < viewBBox.getLonEast()) {

                    Utils.coordinateToPixels(viewWid, viewHei, viewBBox, geoPoint, point);

                    tx = (point.x * dd.x) / pStartSe.x;
                    ty = (point.y * dd.y) / pStartSe.y;

                    drawPointAt(canvas, point.x + pNw.x + tx, point.y + pNw.y + ty, cluster.getSize());
                }
            }
        }
    }

    protected void drawPointAt(Canvas canvas, float x, float y, int count) {
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
        for(IGeoPoint p : algorithm.getItems()) {
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
        algorithm.addItem(point);
    }

    public void addAll(List<IGeoPoint> pointList) {
        algorithm.addItems(pointList);
    }

    @Override
    public BoundingBox getBounds() {
        return boundingBox;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}