package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.ErrorCodes.NETWORK_FAIL;
import static com.teamc.mira.iwashere.data.source.remote.ErrorCodes.UNKNOWN_ERROR;
import static com.teamc.mira.iwashere.data.source.remote.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.ServerUrl.TIMEOUT_TIME_UNIT;

// TODO: 19/04/2017 Implement functions, test those already implemented
public class PoiRepositoryImpl extends AbstractPoiRepository implements PoiRepository {

    public static final String TAG = UserRepositoryImpl.class.getSimpleName();
    private static final String API_POI_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI;
    private static final String API_POI_RATING_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI + ServerUrl.RATING;
    private static final String API_POI_MEDIA_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.POI + ServerUrl.MEDIA;
    private static final String API_POI_CONTENT_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.POI_CONTENT;;

    public PoiRepositoryImpl(Context mContext) {
        super(mContext);
    }

    public PoiRepositoryImpl(RequestQueue requestQueue){
        super(requestQueue);
    }

    @Override
    public PoiModel fetchPoi(String poiId) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        String url = API_POI_URL + "/" + poiId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
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
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONArray response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block

            poi.setPhotos(getMedia(response));


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

        String url =  API_POI_RATING_URL + "/" + poi.getId();
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

        String url = API_POI_RATING_URL + "/" + poi.getId() + "/" + userId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block
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

            final HashMap<String, String> params = getPostRatingParams(poi.getId(), userId, newPoiRating);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_POI_RATING_URL, new JSONObject(params), future, future);
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

        url = url.concat("/"+minLat+"/"+maxLat+"/"+minLong+"/"+maxLong);
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
    public ArrayList<PoiModel> searchPois(String searchQuery) {
        throw new UnsupportedOperationException();
    }


    private void handleError(Exception e) throws RemoteDataException {
        // check to see if the throwable is an instance of the volley error
        if(e.getCause() instanceof VolleyError)
        {
            // grab the volley error from the throwable and cast it back
            VolleyError volleyError = (VolleyError)e.getCause();
            // now just grab the network response like normal
            NetworkResponse networkResponse = volleyError.networkResponse;
            try {
                Log.d(TAG, "raw data: "+ new String(networkResponse.data));
                JSONObject data = new JSONObject(new String(networkResponse.data));

                String code = data.getString("code");

                throw new BasicRemoteException(code);
            } catch (JSONException e1) {
                e1.printStackTrace();
                throw  new BasicRemoteException(UNKNOWN_ERROR);
            }
        }

        if (e instanceof TimeoutException) {
           throw new BasicRemoteException(NETWORK_FAIL);
        }

        if (e instanceof JSONException) {
            throw new BasicRemoteException(ErrorCodes.JSON_PARSING_ERROR);
        }
    }
}
