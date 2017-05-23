package com.teamc.mira.iwashere.presentation.route;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.presentation.main.MapFragment;
import com.teamc.mira.iwashere.presentation.misc.PoiMapMarker;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.list;
import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;
import static com.teamc.mira.iwashere.presentation.route.RouteDetailActivity.EXTRA_ROUTE;

public class RoutePreviewFragment extends MapFragment implements OnMapReadyCallback {
    public static final String TAG = RoutePreviewFragment.class.getSimpleName();

    protected RouteModel route;
    private Map<Marker, PoiModel> poiMap = new HashMap<>();

    private View mRootView;
    private Context mContext;
    private PoiMapMarker poiMapMarker;

    public RoutePreviewFragment(){
        super();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);


        poiMapMarker = new PoiMapMarker(mContext, mGoogleMap);
        if(route != null){
            poiMapMarker.addMarkers(route.getPois());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();


        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            route = (RouteModel) bundle.getSerializable(EXTRA_ROUTE);
        }
    }

}
