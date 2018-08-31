package com.kiryanov.arcgisproject.Clustering;

import org.osmdroid.api.IGeoPoint;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Evgeniy on 30.08.18.
 */

public interface Algorithm<T extends IGeoPoint> {
    void addItem(T item);

    void addItems(Collection<T> items);

    void clearItems();

    void removeItem(T item);

    Set<? extends Cluster<T>> getClusters(double zoom);

    Collection<T> getItems();
}