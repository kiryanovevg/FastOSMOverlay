package com.kiryanov.arcgisproject;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;

/**
 * Created by Evgeniy on 07.08.18.
 */

public class MyMapView extends MapView {

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMapView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

        }

        return super.onTouchEvent(event);
    }
}
