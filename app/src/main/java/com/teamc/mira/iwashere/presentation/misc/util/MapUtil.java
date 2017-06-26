package com.teamc.mira.iwashere.presentation.misc.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.teamc.mira.iwashere.domain.model.PoiModel;

import java.util.ArrayList;

public class MapUtil {

    public static LatLng getNorthEastCorner(ArrayList<PoiModel> pois){
        double lat = Integer.MIN_VALUE, lng = Integer.MIN_VALUE, tempLat, tempLng;

        for (PoiModel poi
                : pois){
            tempLat = poi.getLatitude();
            tempLng = poi.getLongitude();

            if (tempLat > lat) lat = tempLat;
            if (tempLng > lng) lng = tempLng;
        }

        return new LatLng(lat, lng);
    }


    public static LatLng getSouthWest(ArrayList<PoiModel> pois){
        double lat = Integer.MAX_VALUE, lng = Integer.MAX_VALUE, tempLat, tempLng;

        for (PoiModel poi
                : pois){
            tempLat = poi.getLatitude();
            tempLng = poi.getLongitude();

            if (tempLat < lat) lat = tempLat;
            if (tempLng < lng) lng = tempLng;
        }

        return new LatLng(lat, lng);
    }

    public static LatLngBounds getBounds(ArrayList<PoiModel> pois){
        return new LatLngBounds(getSouthWest(pois), getNorthEastCorner(pois));
    }

}
