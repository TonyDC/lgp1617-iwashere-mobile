package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

// TODO: 19/04/2017 Implement functions, test those already implemented
public class PoiRepositoryImpl extends AbstractPOIRepository implements PoiRepository {
    Context mContext;

    public static final String TAG = UserRepositoryImpl.class.getSimpleName();
    public static final String API_POI_URL = "http://192.168.1.69:8080/api/poi/"; // TODO change this
    public static final String API_POI_MEDIA_URL = API_POI_URL + "/media/";
    public static final String API_POI_RATING_URL = API_POI_URL + "/rating/";

    public PoiRepositoryImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public PoiModel fetchPoi(String poiId) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

        String url = API_POI_URL + poiId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block

            return new PoiModel(response);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            handleError(e);
            return null;
        }
    }

    @Override
    public boolean fetchPoiMedia(PoiModel poi) throws RemoteDataException {
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

        String url = API_POI_MEDIA_URL + poi.getId();

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONArray response = future.get(); // this will block

            poi.setPhotos(getMedia(response));

            return true;
        } catch (InterruptedException | ExecutionException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean fetchPoiRating(PoiModel poi) throws RemoteDataException {
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

        String url = API_POI_RATING_URL + poi.getId();

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block

            float currentRating = (float) response.getDouble("rating");
            int currentRatingCount = response.getInt("ratings");
            poi.setRating(currentRating);
            poi.setRatingCount(currentRatingCount);

            return true;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean fetchPoiUserRating(PoiModel poi, String userId) throws RemoteDataException {
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

        String url = API_POI_RATING_URL + poi.getId() + "/" + userId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block

            poi.setUserRating((float) response.getDouble("rating"));
            return true;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean setPoiUserRating(PoiModel poi, String userId, int newPoiRating) throws RemoteDataException {
            // Instantiate the RequestQueue.
            RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

            final HashMap<String, String> params = getPostRatingParams(poi.getId(), userId, newPoiRating);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_POI_RATING_URL, new JSONObject(params), future, future);
            queue.add(request);

            try {
                future.get(); // this will block

                poi.setUserRating(newPoiRating);

                return fetchPoiRating(poi);
            } catch (InterruptedException | ExecutionException e) {
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
                Log.d(TAG, data.toString());

                String code = data.getString("code");

                throw new BasicRemoteException(code);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return;
            }
        }
        e.printStackTrace();
    }
}
