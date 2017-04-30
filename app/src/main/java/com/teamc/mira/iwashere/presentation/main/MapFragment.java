package com.teamc.mira.iwashere.presentation.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.SearchRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PoiMapInteractor;
import com.teamc.mira.iwashere.domain.interactors.SearchInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiMapInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.SearchInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.SearchModel;
import com.teamc.mira.iwashere.presentation.list.ChildRow;
import com.teamc.mira.iwashere.presentation.list.MyExpandableListAdapter;
import com.teamc.mira.iwashere.presentation.list.ParentRow;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragment extends Fragment implements
//        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
//        GoogleMap.OnCameraMoveCanceledListener,
//        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, OnSearchViewListener {


    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MAX_NAME_LENGTH = 30;
    private static final String INTENT_NEW_LOCATION = "New Location";
    public static final float ZOOM = 14.0f;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    //private FilterMaterialSearchView searchView;

    private static double mLatitude;
    private static double mLongitude;
    private boolean mFirstZoomFlag = false;

    HashMap<Marker, PoiModel> poiHashMap = new HashMap<>();

    //Variables needed to keep status of the last call in order to avoid overcalling the onCameraMove function
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
    private long mLastCallMs = Long.MIN_VALUE;
    private LatLngBounds mCurrentCameraBounds;

    private SearchManager searchManager;
    private BaseMaterialSearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private MenuItem searchItem;
    View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_map, container, false);

        Toolbar myToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        searchView = (BaseMaterialSearchView) mRootView.findViewById(R.id.sv);
        searchView.setOnSearchViewListener(this);
        setHasOptionsMenu(true);
        getActivity().setTitle(null);
        parentList = new ArrayList<ParentRow>();
        showTheseParentList = new ArrayList<ParentRow>();

        // The app will crash if display list is not called here.
        displayList(mRootView);

        // This expands the list.
        expandAll();

        // registering receivers for certain intents
        IntentFilter intentNewLocation = new IntentFilter(INTENT_NEW_LOCATION);
        getActivity().getApplicationContext().registerReceiver(mReceiver, intentNewLocation);

        mLatitude = 0;
        mLongitude = 0;

        mMapView = (MapView) mRootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        return mRootView;
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
                intent.putExtra("poi", poiHashMap.get(marker));
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
                if (message == null || message.length() == 0)
                    message = getString(R.string.error_fetch);

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkError() {
                Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
            }
        };

        PoiMapInteractorImpl poiMapInteractor = new PoiMapInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                callBack,
                new PoiRepositoryImpl(getContext()),
                minLat, maxLat, minLng, maxLng
        );
        poiMapInteractor.execute();

        Log.d(TAG, "Lat: " + minLat + " - " + maxLat + " ; Lng: " + minLng + " - " + maxLng);
    }

    private void onPoiFetch(ArrayList<PoiModel> poiModels) {
        PoiModel model;
        for (int i = 0; i < poiModels.size(); i++) {
            model = poiModels.get(i);

            // TODO: 21/04/2017 Added markers in other way to avoid adding existing markers
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(new Double(model.getLatitude()), new Double(model.getLongitude())));
            markerOptions.title(model.getName());

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_primary));

            Marker marker = mGoogleMap.addMarker(markerOptions);

            poiHashMap.put(marker, model);

            Log.d(TAG, "POI MARKER: " + model.getName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

    }

    private void searchForResults(String query) {

        SearchInteractor.CallBack callBack = new SearchInteractor.CallBack() {
            @Override
            public void onSuccess(SearchModel searchModel) {
                Log.d(TAG, "PoiMapInteractor.CallBack SEARCH onSuccess");
                Toast.makeText(getActivity(), "onSuccess SEARCH", Toast.LENGTH_SHORT).show();
                onSearchPoiFetch(searchModel);
            }

            @Override
            public void onFail(String message) {
                if (message == null || message.length() == 0)
                    message = getString(R.string.error_fetch);

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkError() {
                Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
            }
        };

        SearchInteractorImpl searchInteractor = new SearchInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                callBack,
                new SearchRepositoryImpl(getContext()),
                query, mLatitude, mLongitude
        );
        searchInteractor.execute();

        Log.d(TAG, "Searched Query: " + query + " Lat: " + mLatitude + " Lng: " + mLongitude);
    }

    private void onSearchPoiFetch(SearchModel searchModel) {
        PoiModel model;


        parentList = new ArrayList<ParentRow>();
        ArrayList<ChildRow> childRowsPlaces = new ArrayList<ChildRow>();
        ArrayList<ChildRow> childRowsTags = new ArrayList<ChildRow>();
        ParentRow parentRow = null;
        /**
        for (int i = 0; i < poiModels.size(); i++) {
            model = poiModels.get(i);
            Log.d(TAG, "POI MODEL SEARCH: " + model.getName());
            childRowsPlaces.add(new ChildRow(R.drawable.ic_location_on_black_32dp, model.getName()));
            childRowsTags.add(new ChildRow(R.drawable.ic_pound, model.getName()));
        }
        parentRow = new ParentRow("Places", childRowsPlaces);
        parentList.add(parentRow);

        parentRow = new ParentRow("Tags", childRowsTags);
        parentList.add(parentRow);
        displayList(mRootView);
        expandAll();**/
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            myList.expandGroup(i);
        } //end for (int i = 0; i < count; i++)
    }

    private void displayList(View rooView) {
        myList = (ExpandableListView) rooView.findViewById(R.id.expandableListView_search);
        listAdapter = new MyExpandableListAdapter(getActivity(), parentList);
        myList.setAdapter(listAdapter);
    }

    @Override
    public void onSearchViewShown() {
        Toast.makeText(getActivity(), "onSearchViewShown", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchViewClosed() {

        Toast.makeText(getActivity(), "onSearchViewClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getActivity(), "onQueryTextSubmit: " + query, Toast.LENGTH_SHORT).show();
        listAdapter.filterData(query);
        expandAll();
        return true;
    }

    @Override
    public void onQueryTextChange(String newText) {
        Toast.makeText(getActivity(), "onQueryTextChange: " + newText, Toast.LENGTH_SHORT).show();
        listAdapter.filterData(newText);
        if (newText.length() != 0){
            searchForResults(newText);
            expandAll();
        }
    }
}