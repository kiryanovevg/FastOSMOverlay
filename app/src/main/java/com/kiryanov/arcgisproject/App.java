package com.kiryanov.arcgisproject;

import android.support.multidex.MultiDexApplication;

import com.mapbox.mapboxsdk.Mapbox;

/**
 * Created by Evgeniy on 20.07.18.
 */

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        Mapbox.getInstance(this, "pk.eyJ1IjoidmFvbmp4a3giLCJhIjoiY2pqdjhoM3F4MGMwYjNrcGF5bGw4aTVzYiJ9.aE5FZczRzDYoiPa8KrthLw");
        super.onCreate();
    }
}
