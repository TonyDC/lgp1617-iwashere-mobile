package com.teamc.mira.iwashere.presentation.route;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.presentation.misc.MapFragment;
import com.teamc.mira.iwashere.presentation.misc.PoiMapMarker;
import com.teamc.mira.iwashere.presentation.misc.util.MapUtil;

import java.util.HashMap;
import java.util.Map;

import static com.teamc.mira.iwashere.presentation.route.RouteDetailActivity.EXTRA_ROUTE;

public class RoutePreviewFragment extends MapFragment implements OnMapReadyCallback {
    public static final String TAG = RoutePreviewFragment.class.getSimpleName();
    public static final float ZOOM = 12.0f;

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
            poiMapMarker.zoomAroundMarkers();

            /*if (route.getPois().size() > 0) {
                CameraUpdate cu;
                if (route.getPois().size() == 1){
                    PoiModel poi = route.getPois().get(0);

                    LatLng center;
                    center = new LatLng(poi.getLatitude(), poi.getLongitude());

                    cu = CameraUpdateFactory.newLatLngZoom(center, ZOOM);
                }else {
                    LatLngBounds bounds = MapUtil.getBounds(route.getPois());
                    cu = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
                }

                googleMap.moveCamera(cu);
            }*/

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
