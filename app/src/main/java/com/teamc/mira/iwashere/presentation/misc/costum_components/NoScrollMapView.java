package com.teamc.mira.iwashere.presentation.misc.costum_components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class NoScrollMapView extends MapView {
    public NoScrollMapView(Context context) {
        super(context);
    }

    public NoScrollMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NoScrollMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public NoScrollMapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }

        // Handle MapView's touch events.
        super.onTouchEvent(ev);
        return true;
    }
}
