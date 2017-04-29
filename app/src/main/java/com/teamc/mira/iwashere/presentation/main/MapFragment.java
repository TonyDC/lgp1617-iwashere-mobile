package com.teamc.mira.iwashere.presentation.main;

import android.Manifest; import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.PoiRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PoiMapInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiMapInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.util.ArrayList;
import java.util.HashMap;

import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;

public class MapFragment extends Fragment implements
//        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
//        GoogleMap.OnCameraMoveCanceledListener,
//        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String INTENT_NEW_LOCATION = "New Location";
    public static final float ZOOM = 14.0f;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private static double mLatitude;
    private static double mLongitude;
    private boolean mFirstZoomFlag = false;

    HashMap<Marker, PoiModel> poiHashMap = new HashMap<>();

    //Variables needed to keep status of the last call in order to avoid overcalling the onCameraMove function
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
    private long mLastCallMs = Long.MIN_VALUE;
    private LatLngBounds mCurrentCameraBounds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // registering receivers for certain intents
        IntentFilter intentNewLocation = new IntentFilter(INTENT_NEW_LOCATION);
        getActivity().getApplicationContext().registerReceiver(mReceiver, intentNewLocation);

        mLatitude = 0;
        mLongitude = 0;

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setOnCameraMoveListener(this);
        // Initialized for onCameraMoveListener to use
        mCurrentCameraBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), PoiDetailActivity.class);
                intent.putExtra(POI, poiHashMap.get(marker));
                startActivity(intent);
            }
        });

        // Set flag so that it that the map starts on the current location
        mFirstZoomFlag = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void updateCurrentLocation(LatLng latLng) {
        //move map camera
        if (!mFirstZoomFlag) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
            mFirstZoomFlag = true;
        }
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "Location Permission Denied", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Location Permission Denied.");
                }
            }
        }
    }

    // registering BroadcastReceiver for receiving intents from other components
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction(); // getting action from intent

            switch (action) {   // switch for an action
                case "New Location":
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Log.i(TAG, "Location Received.");
                        String latitude = bundle.getString("latitude");
                        String longitude = bundle.getString("longitude");
                        mLatitude = Double.parseDouble(latitude);
                        mLongitude = Double.parseDouble(longitude);
                        updateCurrentLocation(new LatLng(mLatitude, mLongitude));
                    }
                    break;
            }
        }
    };

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

        PoiMapInteractor.CallBack callBack = new PoiMapInteractor.CallBack() {
            @Override
            public void onSuccess(ArrayList<PoiModel> poiModels) {
                Log.d(TAG, "PoiMapInteractor.CallBack onSuccess");
                onPoiFetch(poiModels);
            }
            @Override
            public void onFail(String message) {
                if (isAdded()) {
                    if(message == null || message.length() == 0) message = getResources().getString(R.string.error_fetch);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNetworkError() {
                if (isAdded()) {
                    Toast.makeText(getContext(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                }
            }
        };

        PoiMapInteractorImpl poiMapInteractor = new PoiMapInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                callBack,
                new PoiRepositoryImpl(getContext()),
                minLat,maxLat,minLng,maxLng
        );
        poiMapInteractor.execute();

        Log.d(TAG, "Lat: "+minLat+" - "+maxLat +" ; Lng: "+minLng+" - "+maxLng);
    }

    private void onPoiFetch(ArrayList<PoiModel> poiModels) {
        PoiModel model;
        for (int i = 0; i < poiModels.size(); i++) {
            model = poiModels.get(i);

            // TODO: 21/04/2017 Added markers in other way to avoid adding existing markers
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(new Double(model.getLatitude()),new Double(model.getLongitude())));
            markerOptions.title(model.getName());

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_primary));

            Marker marker = mGoogleMap.addMarker(markerOptions);

            poiHashMap.put(marker,model);

            Log.d(TAG, "POI MARKER: "+model.getName());
        }
    }


}