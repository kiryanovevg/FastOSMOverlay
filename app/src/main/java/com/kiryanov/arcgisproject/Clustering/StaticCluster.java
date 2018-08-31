package com.kiryanov.arcgisproject.Clustering;

import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Evgeniy on 31.08.18.
 */

public class StaticCluster<T extends IGeoPoint> implements Cluster<T> {
    private final IGeoPoint mCenter;
    private final List<T> mItems = new ArrayList<T>();

    public StaticCluster(IGeoPoint center) {
        mCenter = center;
    }

    public boolean add(T t) {
        return mItems.add(t);
    }

    @Override
    public IGeoPoint getPosition() {
        return mCenter;
    }

    public boolean remove(T t) {
        return mItems.remove(t);
    }

    @Override
    public Collection<T> getItems() {
        return mItems;
    }

    @Override
    public int getSize() {
        return mItems.size();
    }

    @Override
    public String toString() {
        return "StaticCluster{" +
                "mCenter=" + mCenter +
                ", mItems.size=" + mItems.size() +
                '}';
    }
}
