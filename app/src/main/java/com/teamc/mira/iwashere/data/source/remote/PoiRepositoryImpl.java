package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

// TODO: 19/04/2017 Implement functions, test those already implemented
public class PoiRepositoryImpl extends AbstractPOIRepository implements PoiRepository {
    Context mContext;

    public static final String TAG = UserRepositoryImpl.class.getSimpleName();
    public static final String API_POI_URL = "http://192.168.1.69:8080/api/poi/"; // TODO change this
    public static final String API_POI_RATING_URL = API_POI_URL + "/rating";


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
    public PoiModel fetchPoiRating(String poiId) throws RemoteDataException {
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_POI_RATING_URL, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block

            PoiModel poiRating = new PoiModel();
            poiRating.setRating((float) response.getDouble("rating"));
            poiRating.setRatingCount((int) response.getInt("ratings"));
            return poiRating;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            handleError(e);
            return null;
        }
    }

    @Override
    public PoiModel fetchPoiUserRating(String poiId, String userId) throws RemoteDataException {
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

        String url = API_POI_RATING_URL + "/" + poiId + "/" + userId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block

            PoiModel poiRating = new PoiModel();
            poiRating.setUserRating((float) response.getDouble("rating"));
            return poiRating;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            handleError(e);
            return null;
        }
    }

    @Override
    public PoiModel setPoiUserRating(String poiId, String userId, int newPoiRating) throws RemoteDataException {
            // Instantiate the RequestQueue.
            RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

            final HashMap<String, String> params = getPostRatingParams(poiId, userId, newPoiRating);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_POI_RATING_URL, new JSONObject(params), future, future);
            queue.add(request);

            try {
                JSONObject response = future.get(); // this will block

                return fetchPoiRating(poiId);
            } catch (InterruptedException | ExecutionException e) {
                handleError(e);
                return null;
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

                throw (RemoteDataException) new BasicRemoteException(code);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return;
            }
        }
        e.printStackTrace();
        return;
    }
}
