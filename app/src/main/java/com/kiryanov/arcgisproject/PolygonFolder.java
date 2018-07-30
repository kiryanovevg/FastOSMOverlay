package com.kiryanov.arcgisproject;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

/**
 * Created by Evgeniy on 30.07.18.
 */

public class PolygonFolder extends FolderOverlay implements MapListener {

    private boolean simple = true;

    @Override
    public boolean onScroll(ScrollEvent event) {
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        if (event.getZoomLevel() > 11) {
            if (simple) {
                for (Overlay polygon: getItems()) {
                    ((SimplePolygon) polygon).showOriginal();
                }
                simple = false;
            }
        } else {
            if (!simple) {
                for (Overlay polygon: getItems()) {
                    ((SimplePolygon) polygon).showSimple();
                }
                simple = true;
            }
        }
        return false;
    }

    private void add(SimplePolygon polygon) {
        getItems().add(polygon);
    }

    private void addAll(List<SimplePolygon> polygonList) {
        getItems().addAll(polygonList);
    }
}
