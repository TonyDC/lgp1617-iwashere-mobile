package com.teamc.mira.iwashere.presentation.misc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.ContentModel;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.util.ImageResource;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.teamc.mira.iwashere.presentation.misc.MapFragment.PADDING;
import static com.teamc.mira.iwashere.presentation.misc.MapFragment.TAG;
import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;

public class PoiMapMarker {
    private final GoogleMap mGoogleMap;
    private final Context mContext;

    private Map<Marker, PoiModel> poiMap = new HashMap<>();
    private HashSet<PoiModel> poiSet = new HashSet<>();

    private static final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.marker_primary);

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
        });

    }

    public void addMarker(PoiModel poi){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(Double.valueOf(poi.getLatitude()), Double.valueOf(poi.getLongitude())));
        markerOptions.title(poi.getName());

        markerOptions.icon(icon);

        Marker marker = mGoogleMap.addMarker(markerOptions);
        poiMap.put(marker,poi);
        poiSet.add(poi);
    }

    public void addMarkers(ArrayList<PoiModel> list){
        for (PoiModel poi:
             list) {

            addMarker(poi);
        }

        mGoogleMap.setInfoWindowAdapter(new PoiInfoWindowAdapter());
    }

    public void zoomAroundMarkers(){
        if (poiMap.size() == 0) return;

        CameraUpdate cu;
        int padding = PADDING; // offset from edges of the map in pixels

        if (poiMap.size() == 1) {
            cu = CameraUpdateFactory.newLatLngZoom(poiMap.keySet().iterator().next().getPosition(), padding);
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : poiMap.keySet()) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }


        mGoogleMap.moveCamera(cu);
    }



    class PoiInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        PoiInfoWindowAdapter(){
            myContentsView = LayoutInflater.from(mContext).inflate(R.layout.costum_poi_info_window_map, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }


        @Override
        public View getInfoWindow(Marker marker) {
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.poiName));
            tvTitle.setText(marker.getTitle());
            final ImageView imageView = ((ImageView) myContentsView.findViewById(R.id.poiImage));

            ArrayList<Resource> contentList = poiMap.get(marker).getMedia();
            if (contentList.size() > 0) {
                Resource resource = contentList.get(0);
                if (resource instanceof ImageResource) {
                    ((ImageResource) resource).fetchDownloadUrl(ImageResource.Size.SIZE_XSMALL, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            String imageUrl = task.getResult().toString();
                            Log.d(TAG, "Showing picture in " + imageView.toString());
                            Picasso.with(mContext).load(imageUrl).into(imageView);
                        }
                    });

                    Log.d(TAG, "Showing picture of " + poiMap.get(marker).getName());
                }

            }

            return myContentsView;
        }
    }

    public HashSet<PoiModel> getPoiSet() {
        return poiSet;
    }
}
