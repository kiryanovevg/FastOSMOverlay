package com.kiryanov.arcgisproject.Overlay;

import android.graphics.Canvas;
import android.graphics.Point;

import com.kiryanov.arcgisproject.Clustering.ClusteringAlgorithm;
import com.kiryanov.arcgisproject.Clustering.StaticCluster;
import com.kiryanov.arcgisproject.FastOverlay.Utils;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Evgeniy on 07.09.18.
 */

public class ClustersDrawing extends DrawingAlgorithm {

    private ClusteringAlgorithm<IGeoPoint> algorithm = new ClusteringAlgorithm<>();
    private Set clusters;
    private int zoomLevel;
    private int cellSize = 20;

    private Disposable disposable;

    @Override
    public void draw(Canvas canvas, MapView mapView) {
        final Projection pj = mapView.getProjection();
        final BoundingBox viewBBox = mapView.getBoundingBox();

        startBoundingBox = viewBBox;
        startProjection = mapView.getProjection();
        updateGrid(mapView);

        int currentZoomLevel = ((int) mapView.getZoomLevelDouble());
        if (zoomLevel != currentZoomLevel) {
            zoomLevel = currentZoomLevel;

            Single.fromCallable(() -> algorithm.getClusters(mapView.getZoomLevelDouble()))
                    .doOnSubscribe(d -> {
                        if (disposable != null && !disposable.isDisposed())
                            disposable.dispose();

                        disposable = d;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(clusters -> {
                        ClustersDrawing.this.clusters = clusters;
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

    @Override
    protected void updateGrid(MapView mapView) {
        viewWid = mapView.getWidth();
        viewHei = mapView.getHeight();
        gridWid = (int) Math.floor((float) viewWid / cellSize) + 1;
        gridHei = (int) Math.floor((float) viewHei / cellSize) + 1;
    }

    @Override
    protected void add(IGeoPoint point) {
        algorithm.addItem(point);
    }

    @Override
    protected void addAll(List<IGeoPoint> pointList) {
        algorithm.addItems(pointList);
    }

    @Override
    protected void clear() {
        algorithm.clearItems();
    }

    @Override
    protected List<IGeoPoint> getItems() {
        return new ArrayList<>(algorithm.getItems());
    }

    @Override
    protected int getCellSize() {
        return cellSize;
    }
}

