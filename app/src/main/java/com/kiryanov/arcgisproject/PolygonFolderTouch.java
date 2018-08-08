package com.kiryanov.arcgisproject;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.PointReducer;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeniy on 07.08.18.
 */

public class PolygonFolderTouch extends FolderOverlay {

    private GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public boolean onTouchEvent(MotionEvent e, MapView mapView) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
//            hideOutOfBoundsPolygons(mapView);

            Handler handler = new Handler();
            Thread thread = new Thread(() -> hideOutOfBoundsPolygons(mapView, handler));
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
        return super.onTouchEvent(e, mapView);
    }

    private void hideOutOfBoundsPolygons(final MapView mapView, Handler handler) {
        Polygon screen = getScreenPolygon(mapView);

        int count = 0;
        for (Overlay overlay : getItems()) {
            if (overlay instanceof org.osmdroid.views.overlay.Polygon) {
                ArrayList<GeoPoint> points = ((ArrayList<GeoPoint>) ((org.osmdroid.views.overlay.Polygon) overlay).getPoints());
                List<Coordinate> coordinates = new ArrayList<>();

                List<GeoPoint> reducedPoints = PointReducer.reduceWithTolerance(
                        points, getToleranceForReduce(mapView)
                );

                for (GeoPoint point : reducedPoints) {
                    coordinates.add(
                            new Coordinate(point.getLatitude(), point.getLongitude())
                    );
                }

                Polygon item = getPolygonFromCoordinates(
                        coordinates.toArray(new Coordinate[coordinates.size()])
                );

                item.normalize();

                try {
//                    Geometry union = screen.union(item);
                    overlay.setEnabled(screen.intersection(item).getCoordinates().length != 0);
                } catch (TopologyException topologyException) {
                    topologyException.printStackTrace();
//                        count++;
                }

            }
        }

        handler.post(mapView::invalidate);

        count = count;
    }

    private double getToleranceForReduce(MapView mapView) {
        BoundingBox boundingBox = null;
        try {
            boundingBox = mapView.getBoundingBox();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        if (boundingBox != null) {
            final double latSpanDegrees = boundingBox.getLatitudeSpan();
            return latSpanDegrees / mapView.getContext().getResources().getDisplayMetrics().densityDpi;
        } else {
            return 0;
        }

    }

    private Polygon getScreenPolygon(MapView mapView) {
        Projection projection = mapView.getProjection();

        int width = projection.getScreenRect().width();
        int height = projection.getScreenRect().height();

        IGeoPoint leftTop = projection.fromPixels(0, 0);
        IGeoPoint leftBottom = projection.fromPixels(0, height);
        IGeoPoint rightTop = projection.fromPixels(width, 0);
        IGeoPoint rightBottom = projection.fromPixels(width, height);

        /*Log.d("GeoPoint", "leftTop:" + leftTop.toString());
        Log.d("GeoPoint", "leftBottom:" + leftBottom.toString());
        Log.d("GeoPoint", "rightTop:" + rightTop.toString());
        Log.d("GeoPoint", "rightBottom:" + rightBottom.toString());
        Log.d("GeoPoint", "------------");*/

        return getPolygonFromCoordinates(new Coordinate[] {
                new Coordinate(leftTop.getLatitude(), leftTop.getLongitude()),
                new Coordinate(leftBottom.getLatitude(), leftBottom.getLongitude()),
                new Coordinate(rightBottom.getLatitude(), rightBottom.getLongitude()),
                new Coordinate(rightTop.getLatitude(), rightTop.getLongitude()),
                new Coordinate(leftTop.getLatitude(), leftTop.getLongitude())
        });
    }

    private Polygon getPolygonFromCoordinates(Coordinate[] coordinates) {
        CoordinateArraySequence coordinateSequence = new CoordinateArraySequence(coordinates);
        LinearRing linearRing = new LinearRing(coordinateSequence, geometryFactory);
        Polygon result = new Polygon(linearRing, null, geometryFactory);

        return result;
    }

    @Override
    public void draw(Canvas canvas, MapView osm, boolean shadow) {
        super.draw(canvas, osm, shadow);
        int i = 0;
        i = i;
    }
}
