package com.kiryanov.arcgisproject.Clustering;

import org.osmdroid.api.IGeoPoint;

import java.util.Collection;

/**
 * Created by Evgeniy on 30.08.18.
 */

public interface Cluster<T extends IGeoPoint> {
    public IGeoPoint getPosition();

    Collection<T> getItems();

    int getSize();
}
