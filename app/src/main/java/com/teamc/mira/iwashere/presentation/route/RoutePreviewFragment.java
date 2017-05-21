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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.presentation.main.MapFragment;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.list;
import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;

public class RoutePreviewFragment extends MapFragment implements OnMapReadyCallback {
    protected ArrayList<PoiModel> pois = new ArrayList<>();
    protected RouteModel route;


    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private View mRootView;
    private Context mContext;
    private Map<Marker, PoiModel> poiMap = new HashMap<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), PoiDetailActivity.class);
                intent.putExtra(POI, poiMap.get(marker));
                startActivity(intent);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = getContext();

        pois = (ArrayList<PoiModel>) getArguments().getSerializable(RouteDetailActivity.EXTRA_POIS);

        mMapView = (MapView) mRootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
