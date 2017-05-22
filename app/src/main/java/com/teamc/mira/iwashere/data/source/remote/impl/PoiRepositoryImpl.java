package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.AbstractPoiRepository;
import com.teamc.mira.iwashere.data.source.remote.UserRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.util.JsonObjectRequestWithNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT_TIME_UNIT;

// TODO: 19/04/2017 Implement functions, test those already implemented
public class PoiRepositoryImpl extends AbstractPoiRepository implements PoiRepository {

    public static final String TAG = PoiRepositoryImpl.class.getSimpleName();
    private static final String API_POI_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI;
    private static final String API_POI_GET_RATING_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI + ServerUrl.RATING;
    private static final String API_POI_POST_RATING_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI + ServerUrl.AUTH + ServerUrl.RATING;
    private static final String API_POI_MEDIA_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI + ServerUrl.MEDIA;
    private static final String API_POI_CONTENT_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.POI_CONTENT;;

    public PoiRepositoryImpl(Context mContext) {
        super(mContext);
    }

    public PoiRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    @Override
    public PoiModel fetchPoi(String poiId) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        String url = API_POI_URL + "/" + poiId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        Log.d(TAG,url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block


            future.cancel(true);
            return new PoiModel(response);
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return null;
        }
    }

    @Override
    public boolean fetchPoiMedia(PoiModel poi) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        String url = API_POI_MEDIA_URL + "/" + poi.getId();
        Log.d(TAG,url);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONArray response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block
            Log.d(TAG, "fetchPoiMedia raw data: "+response.toString());
            ArrayList<Resource> photos = getMedia(response);
            poi.setPhotos(photos);


            future.cancel(true);
            return true;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean fetchPoiRating(PoiModel poi) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        String url =  API_POI_GET_RATING_URL + "/" + poi.getId();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block

            float currentRating = (float) response.getDouble("rating");
            int currentRatingCount = response.getInt("ratings");
            poi.setRating(currentRating);
            poi.setRatingCount(currentRatingCount);

            future.cancel(true);
            return true;
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean fetchPoiUserRating(PoiModel poi, String userId) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        String url = API_POI_GET_RATING_URL + "/" + poi.getId() + "/" + userId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        Log.d(TAG, url);

        JsonObjectRequestWithNull request = new JsonObjectRequestWithNull(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block

            if(!response.has("rating")){
                poi.setUserRating(0);
//                throw new BasicRemoteException("no-content");
                return true;
            }
            poi.setUserRating((float) response.getDouble("rating"));
            return true;
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean setPoiUserRating(PoiModel poi, String userId, int newPoiRating) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        final HashMap<String, Object> params = getPostRatingParams(poi.getId(), userId, newPoiRating);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_POI_POST_RATING_URL,
                new JSONObject(params),
                future, future){
            @Override
            public HashMap<String, String> getHeaders() {
                return PoiRepositoryImpl.this.getHeaders();
            }
        };

        queue.add(request);

        try {
            future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block
        	poi.setUserRating(newPoiRating);
            return fetchPoiRating(poi);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean fetchPoiContent(PoiModel poi, String userId, int contentOffset, int contentLimit) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        String url = API_POI_CONTENT_URL;
        if (userId == null) {
            url += "/" + userId;
        }
        url += "/" + poi.getId() + "/" + contentOffset + "/" + contentLimit;

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONArray response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block

            poi.setContent(getContent(response));

            return true;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public PoiModel setReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel removeReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayList<PoiModel> fetchPoisInArea(double maxLat, double minLat, double maxLong, double minLong) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;
        String url = ServerUrl.getUrl()+ServerUrl.API+ServerUrl.POI+ServerUrl.RANGE;

        url = url.concat("/" + minLat + "/" + maxLat + "/" + minLong + "/" + maxLong);
        Log.d(TAG, url);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try{
            JSONArray response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block
            System.out.println(TAG+": " + String.valueOf(response));

            ArrayList<PoiModel> poiModels = new ArrayList<PoiModel>();
            PoiModel poiModel;
            JSONObject object;

            for (int i = 0; i < response.length(); i++) {
                object = response.getJSONObject(i);
                poiModel = new PoiModel(object);
                poiModels.add(poiModel);
            }

            return poiModels;
        }catch (InterruptedException | ExecutionException | TimeoutException | JSONException e){
            handleError(e);
            return null;
        }
    }

    @Override
    public ArrayList<PoiModel> searchPois(String searchQuery, double lat, double lng) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        String url = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI + ServerUrl.SEARCH + "?query=";

        url = url.concat(searchQuery + "&" + lat + "&" + lng);
        Log.d(TAG, url);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        return getPoiSearchModelsFromRequest(future);
    }

    @Nullable
    ArrayList<PoiModel> getPoiSearchModelsFromRequest(RequestFuture<JSONObject> future) throws RemoteDataException {
        try {
            JSONObject response = future.get(3000, TimeUnit.MILLISECONDS); // this will block
            System.out.println(TAG + ": " + String.valueOf(response));

            ArrayList<PoiModel> poiModels = new ArrayList<PoiModel>();
            PoiModel poiModel;
            JSONObject object;

            JSONArray results = response.getJSONArray("results");
            System.out.println(TAG + ": " + String.valueOf(results));

            for (int i = 0; i < results.length(); i++) {
                object = results.getJSONObject(i);
                poiModel = new PoiModel(object);
                poiModels.add(poiModel);
            }

            return poiModels;
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return null;
        }
    }

}
