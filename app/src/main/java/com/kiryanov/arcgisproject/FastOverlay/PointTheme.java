package com.kiryanov.arcgisproject.FastOverlay;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.StyledLabelledGeoPoint;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Evgeniy on 14.08.18.
 */

public final class PointTheme implements FastPointOverlay.PointAdapter {
    private final List<IGeoPoint> mPoints;
    private boolean mLabelled, mStyled;

    public PointTheme(List<IGeoPoint> pPoints) {
        this(pPoints, pPoints.size() != 0 && pPoints.get(0) instanceof LabelledGeoPoint
                , pPoints.size() != 0 && pPoints.get(0) instanceof StyledLabelledGeoPoint);
    }

    public PointTheme(List<IGeoPoint> pPoints, boolean labelled) {
        this(pPoints, labelled, false);
    }

    public PointTheme(List<IGeoPoint> pPoints, boolean labelled, boolean styled) {
        mPoints = pPoints;
        mLabelled = labelled;
        mStyled = styled;
    }

    @Override
    public int size() {
        return mPoints.size();
    }

    @Override
    public IGeoPoint get(int i) {
        return mPoints.get(i);
    }

    @Override
    public boolean isLabelled() {
        return mLabelled;
    }

    @Override
    public boolean isStyled() {
        return mStyled;
    }

    /**
     * NOTE: this iterator will be called very frequently, avoid complicated code.
     * @return
     */
    @Override
    public Iterator<IGeoPoint> iterator() {
        return mPoints.iterator();
    }

}
