package com.kiryanov.arcgisproject;

/**
 * Created by Evgeniy on 27.07.18.
 */

/*
public class FastPolygon extends Polygon {

    private List<GeoPoint> original = new ArrayList<>();

    @Override
    public void showInfoWindow() {
        closeInfoWindow();
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        */
/*super.setPoints(original);

        int maxPoints = 100 * ((int) mapView.getZoomLevelDouble());
        int step = getPoints().size() / maxPoints;

        for (int i = step; i < getPoints().size(); i += step) {
            getPoints().remove(i);
        }

        mapView.invalidate();*//*


        super.draw(canvas, mapView, shadow);
    }

    @Override
    public void setPoints(List<GeoPoint> points) {
        original.addAll(points);

        super.setPoints(points);
    }
}*/
