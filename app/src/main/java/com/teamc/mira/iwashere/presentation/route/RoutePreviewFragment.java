package com.teamc.mira.iwashere.presentation.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;

import java.util.ArrayList;

public class RoutePreviewFragment extends Fragment implements OnMapReadyCallback {
    protected ArrayList<PoiModel> pois = new ArrayList<>();
    protected RouteModel route;


    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private View mRootView;

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);

        pois = (ArrayList<PoiModel>) getArguments().getSerializable(RouteDetailActivity.EXTRA_POIS);
        route = (RouteModel) getArguments().getSerializable(RouteDetailActivity.EXTRA_ROUTE);

        mMapView = (MapView) mRootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
