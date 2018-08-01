package com.kiryanov.arcgisproject;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Evgeniy on 30.07.18.
 */

public class PolygonFolder extends FolderOverlay implements MapListener {

    private boolean simple = true;

    @Override
    public boolean onScroll(ScrollEvent event) {
//        hideOutOfBoundsPolygonsOnePoint(event.getSource());
        hideOutOfBoundsPolygonsAllPoint(event.getSource());

        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        if (event.getZoomLevel() > 11) {
            if (simple) {
                for (Overlay polygon: getItems()) {
                    if (polygon instanceof SimplePolygon) {
                        ((SimplePolygon) polygon).showOriginal();
                    }
                }
                simple = false;
            }
        } else {
            if (!simple) {
                for (Overlay polygon: getItems()) {
                    if (polygon instanceof  SimplePolygon) {
                        ((SimplePolygon) polygon).showSimple();
                    }
                }
                simple = true;
            }
        }

//        hideOutOfBoundsPolygonsOnePoint(event.getSource());
        hideOutOfBoundsPolygonsAllPoint(event.getSource());

        return false;
    }

    //Если хотя бы одна точка выходит за границы
    private void hideOutOfBoundsPolygonsOnePoint(MapView mapView) {
        BoundingBox bounds = null;
        try {
            bounds = mapView.getBoundingBox();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        if (bounds != null) {
            for (Overlay overlay : getItems()) {
                if (overlay instanceof SimplePolygon) {
                    List<GeoPoint> points = ((SimplePolygon) overlay).getPoints();

                    boolean show = true;

                    for (GeoPoint point : points) {
                        boolean inLatitudeBounds = point.getLatitude() < bounds.getLatNorth() &&
                                point.getLatitude() > bounds.getLatSouth();

                        boolean inLongitudeBounds = point.getLongitude() < bounds.getLonEast() &&
                                point.getLongitude() > bounds.getLonWest();

                        if (!inLongitudeBounds || !inLatitudeBounds) {
                            if (overlay.isEnabled()) {
                                overlay.setEnabled(false);
                            }

                            show = false;
                            break;
                        }
                    }

                    if (show && !overlay.isEnabled()) {
                        overlay.setEnabled(true);
                    }
                }
            }
        }
    }

    //Если все точки не видны
    private void hideOutOfBoundsPolygonsAllPoint(MapView mapView) {
        BoundingBox bounds = null;
        try {
            bounds = mapView.getBoundingBox();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        if (bounds != null) {
            for (Overlay overlay : getItems()) {
                if (overlay instanceof SimplePolygon) {
                    List<GeoPoint> points = ((SimplePolygon) overlay).getPoints();

                    boolean show = false;

                    for (GeoPoint point : points) {
                        boolean inLatitudeBounds = point.getLatitude() < bounds.getLatNorth() &&
                                point.getLatitude() > bounds.getLatSouth();

                        boolean inLongitudeBounds = point.getLongitude() < bounds.getLonEast() &&
                                point.getLongitude() > bounds.getLonWest();

                        if (inLongitudeBounds && inLatitudeBounds) {
                            show = true;
                            break;
                        }
                    }

                    if (show) {
                        if (!overlay.isEnabled()) {
                            overlay.setEnabled(true);
                        }
                    } else {
                        if (overlay.isEnabled()) {
                            overlay.setEnabled(false);
                        }
                    }
                }
            }
        }
    }
}
