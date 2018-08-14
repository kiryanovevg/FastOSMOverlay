package com.kiryanov.arcgisproject.FastOverlay;

import android.graphics.Point;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;

/**
 * Created by Evgeniy on 14.08.18.
 */

public class Utils {

    public static void coordinateToPixels(int viewWid, int viewHei,
                                          BoundingBox viewBBox,
                                          IGeoPoint pt1, Point mPositionPixels) {
        double dx = viewBBox.getLonEast() - viewBBox.getLonWest();
        double xRatio = pt1.getLongitude() - viewBBox.getLonWest();
        if (xRatio <= 0) return;

        double xPercent = xRatio / dx;
        int px = ((int) (viewWid * xPercent));

        double dy = viewBBox.getLatNorth() - viewBBox.getLatSouth();
        double yRatio = pt1.getLatitude() - viewBBox.getLatSouth();
        if (yRatio <= 0) return;

        double yPercent = yRatio / dy;
        int py = ((int) (viewHei * yPercent));

        mPositionPixels.set(px, viewHei - py);
    }
}
