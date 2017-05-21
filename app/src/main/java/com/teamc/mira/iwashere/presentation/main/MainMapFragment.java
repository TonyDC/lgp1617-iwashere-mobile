package com.teamc.mira.iwashere.presentation.main;

import android.Manifest;
import android.app.AlertDialog;
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
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.impl.SearchRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PoiMapInteractor;
import com.teamc.mira.iwashere.domain.interactors.SearchInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiMapInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.SearchInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.domain.model.SearchModel;
import com.teamc.mira.iwashere.domain.model.TagModel;
import com.teamc.mira.iwashere.presentation.searchList.ChildRow;
import com.teamc.mira.iwashere.presentation.searchList.MyExpandableListAdapter;
import com.teamc.mira.iwashere.presentation.searchList.ParentRow;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.util.ArrayList;
import java.util.HashMap;

import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;

public class MainMapFragment extends MapFragment implements
//        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
//        GoogleMap.OnCameraMoveCanceledListener,
//        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.OnConnectionFailedListener,
        OnSearchViewListener {

    private static final String TAG = MainMapFragment.class.getSimpleName();
    private static final String INTENT_NEW_LOCATION = "New Location";
    public static final float ZOOM = 14.0f;
    private static final LatLng PORTO_LAT_LNG = new LatLng(41.1485647, -8.6119707);

    private static double mLatitude;
    private static double mLongitude;
    private boolean mFirstZoomFlag = false;

    HashMap<Marker, PoiModel> poiHashMap = new HashMap<>();

    //Variables needed to keep status of the last call in order to avoid overcalling the onCameraMove function
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
    private long mLastCallMs = Long.MIN_VALUE;
    private LatLngBounds mCurrentCameraBounds;

    private BaseMaterialSearchView mSearchView;
    private MyExpandableListAdapter mSearchListAdapter;
    private ExpandableListView mSearchList;
    private ArrayList<ParentRow> mCategoriesList = new ArrayList<>();
    private View mRootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_main_map, container, false);

        Toolbar myToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        mSearchView = (BaseMaterialSearchView) mRootView.findViewById(R.id.sv);
        mSearchView.setOnSearchViewListener(this);

        setHasOptionsMenu(true);
        getActivity().setTitle(null);
        mCategoriesList = new ArrayList<>();
        displaySearchResults(mRootView);
        expandCategories();

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

        mContext = getContext();

        mMapView.getMapAsync(this);
        return mRootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

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
        mFirstZoomFlag = true;


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PORTO_LAT_LNG, ZOOM));
        fetchPoisOnCameraMove(mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
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
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNetworkError() {
                if (isAdded()) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_SHORT).show();
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
        PoiModel model;
        for (int i = 0; i < poiModels.size(); i++) {
            model = poiModels.get(i);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(Double.valueOf(model.getLatitude()), Double.valueOf(model.getLongitude())));
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
        mSearchView.setMenuItem(item);

    }

    private void searchForResults(String query) {

        SearchInteractor.CallBack callBack = new SearchInteractor.CallBack() {
            @Override
            public void onSuccess(SearchModel searchModel) {
                Log.d(TAG, "PoiMapInteractor.CallBack SEARCH onSuccess");
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

        mCategoriesList = new ArrayList<>();

        if (!searchModel.getPois().isEmpty()) {
            ArrayList<ChildRow> childRowsPois = new ArrayList<>();
            PoiModel model;
            ParentRow parentRow;
            for (int i = 0; i < searchModel.getPois().size(); i++) {
                model = searchModel.getPois().get(i);
                Log.d(TAG, "POI MODEL SEARCH: " + model.getName());
                childRowsPois.add(new ChildRow(R.drawable.map_marker, model));
            }
            parentRow = new ParentRow("Points of Interest", childRowsPois);
            mCategoriesList.add(parentRow);
        }

        if (!searchModel.getRoutes().isEmpty()) {
            ArrayList<ChildRow> childRowsRoutes = new ArrayList<>();
            RouteModel model;
            ParentRow parentRow;
            for (int i = 0; i < searchModel.getRoutes().size(); i++) {
                model = searchModel.getRoutes().get(i);
                Log.d(TAG, "ROUTE MODEL SEARCH: " + model.getName());
                childRowsRoutes.add(new ChildRow(R.drawable.walk, model));
            }
            parentRow = new ParentRow("Routes", childRowsRoutes);
            mCategoriesList.add(parentRow);
        }

        if (!searchModel.getTags().isEmpty()) {
            ArrayList<ChildRow> childRowsTags = new ArrayList<>();
            TagModel model;
            ParentRow parentRow;
            for (int i = 0; i < searchModel.getTags().size(); i++) {
                model = searchModel.getTags().get(i);
                Log.d(TAG, "TAG MODEL SEARCH: " + model.getName());
                childRowsTags.add(new ChildRow(R.drawable.pound, model));
            }
            parentRow = new ParentRow("Tags", childRowsTags);
            mCategoriesList.add(parentRow);
        }

        displaySearchResults(mRootView);
        expandCategories();
    }

    private void expandCategories() {
        int count = mSearchListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mSearchList.expandGroup(i);
        }
    }

    private void displaySearchResults(View rooView) {
        mSearchList = (ExpandableListView) rooView.findViewById(R.id.expandableListView_search);
        mSearchListAdapter = new MyExpandableListAdapter(getActivity(), mCategoriesList);
        mSearchList.setAdapter(mSearchListAdapter);
    }

    @Override
    public void onSearchViewShown() {
        Log.d(TAG, "onSearchViewShown: clicked");
    }

    @Override
    public void onSearchViewClosed() {
        Log.d(TAG, "onSearchViewClosed: clicked");
        mCategoriesList = new ArrayList<>();
        displaySearchResults(mRootView);
        expandCategories();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);
        if (query.length() != 0) {
            searchForResults(query);
        } else {
            mCategoriesList = new ArrayList<>();
            displaySearchResults(mRootView);
        }
        expandCategories();
        return true;
    }

    @Override
    public void onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: " + newText);
        if (newText.length() != 0) {
            searchForResults(newText);
        } else {
            mCategoriesList = new ArrayList<>();
            displaySearchResults(mRootView);
        }
        expandCategories();
    }
}