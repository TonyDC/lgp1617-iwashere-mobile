package com.teamc.mira.iwashere.presentation.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiMapInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.presentation.misc.LocationBasedMapFragment;
import com.teamc.mira.iwashere.presentation.misc.PoiMapMarker;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.util.ArrayList;


public class PoiMapFragment extends LocationBasedMapFragment implements
        GoogleMap.OnCameraMoveListener{

    public static final float ZOOM = 14.0f;
    private static final LatLng PORTO_LAT_LNG = new LatLng(41.1485647, -8.6119707);

    private boolean mFirstZoomFlag = false;

    //Variables needed to keep status of the last call in order to avoid overcalling the onCameraMove function
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
    private long mLastCallMs = Long.MIN_VALUE;
    private LatLngBounds mCurrentCameraBounds;

    private PoiMapMarker poiMapMarker;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance) {
        View mRootView = super.onCreateView(layoutInflater, viewGroup, savedInstance);

        getMapAsync(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContext = getContext();

        return mRootView;
    }

    @Override
    protected void updateCurrentLocation(LatLng latLng) {
        //move map camera
        if (!mFirstZoomFlag) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
            mFirstZoomFlag = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mGoogleMap.setOnCameraMoveListener(this);
        // Initialized for onCameraMoveListener to use
        mCurrentCameraBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        poiMapMarker = new PoiMapMarker(mContext, mGoogleMap);

        // Set flag so that it that the map starts on the current location
        mFirstZoomFlag = true;


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PORTO_LAT_LNG, ZOOM));
        fetchPoisOnCameraMove(mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
    }

    @Override
    public void onCameraMove() {
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        // Check whether the camera changes report the same boundaries (?!), yes, it happens
        if (mCurrentCameraBounds.northeast.latitude == bounds.northeast.latitude
                && mCurrentCameraBounds.northeast.longitude == bounds.northeast.longitude
                && mCurrentCameraBounds.southwest.latitude == bounds.southwest.latitude
                && mCurrentCameraBounds.southwest.longitude == bounds.southwest.longitude) {
            return;
        }

        final long snap = System.currentTimeMillis();
        if (mLastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap) {
            mLastCallMs = snap;
            return;
        }

        //Store cache fields
        mLastCallMs = snap;
        mCurrentCameraBounds = bounds;

        //Fetch data
        fetchPoisOnCameraMove(bounds);
    }

    private void fetchPoisOnCameraMove(LatLngBounds bounds) {
        LatLng northeast = bounds.northeast;
        LatLng southwest = bounds.southwest;

        double minLat, maxLat, minLng, maxLng;
        minLat = southwest.latitude;
        maxLat = northeast.latitude;
        minLng = southwest.longitude;
        maxLng = northeast.longitude;

        TemplateInteractor.CallBack callBack = new TemplateInteractor.CallBack<ArrayList<PoiModel>>() {
            @Override
            public void onSuccess(ArrayList<PoiModel> poiModels) {
                Log.d(TAG, "PoiMapInteractor.CallBack onSuccess");
                onPoiFetch(poiModels);
            }

            @Override
            public void onNetworkError() {
                if (isAdded()) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String code, String message) {

                if (isAdded()) {
                    if(message == null || message.length() == 0) message = getResources().getString(R.string.error_fetch);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        };

        PoiMapInteractorImpl poiMapInteractor = new PoiMapInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                callBack,
                new PoiRepositoryImpl(mContext),
                minLat, maxLat, minLng, maxLng
        );
        poiMapInteractor.execute();

        Log.d(TAG, "Lat: " + minLat + " - " + maxLat + " ; Lng: " + minLng + " - " + maxLng);
    }

    private void onPoiFetch(ArrayList<PoiModel> poiModels) {
        poiMapMarker.addMarkers(poiModels);
    }

    public LatLng getPosition(){
        return  new LatLng(mLatitude, mLongitude);
    }
}
