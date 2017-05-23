package com.teamc.mira.iwashere.presentation.misc;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;

public class PoiMapMarker {
    private final GoogleMap mGoogleMap;
    private final Context mContext;

    private Map<Marker, PoiModel> poiMap = new HashMap<>();

    public PoiMapMarker(Context context, GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mContext = context;

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(mContext, PoiDetailActivity.class);
                intent.putExtra(POI, poiMap.get(marker));
                mContext.startActivity(intent);
            }
        });    }

    public void addMarker(PoiModel poi){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(Double.valueOf(poi.getLatitude()), Double.valueOf(poi.getLongitude())));
        markerOptions.title(poi.getName());

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_primary));

        Marker marker = mGoogleMap.addMarker(markerOptions);
        poiMap.put(marker,poi);
    }

    public void addMarkers(ArrayList<PoiModel> list){
        for (PoiModel poi:
             list) {
            addMarker(poi);
        }
    }
}
