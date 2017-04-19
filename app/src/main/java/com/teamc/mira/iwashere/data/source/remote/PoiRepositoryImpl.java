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

    public PoiRepositoryImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public PoiModel fetchPoi(String id) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel fetchPoi(PoiModel poi) throws RemoteDataException {
        return fetchPoi(poi.getId());
    }

    @Override
    public PoiModel getPoiRating(String poiId, String userId) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel setPOIRating(String poiId, String userId, int newPoiRating) throws RemoteDataException {
            // Instantiate the RequestQueue.
            RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();

            // TODO change this
            String url ="http://192.168.1.69:8080/api/poi/rating/";

            final HashMap<String, String> params = getPostRatingParams(poiId, userId, newPoiRating);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), future, future);
            queue.add(request);

            try {
                JSONObject response = future.get(); // this will block

                return getPoiDetails(response);
            } catch (InterruptedException | ExecutionException e) {
                //check to see if the throwable in an instance of the volley error
                if(e.getCause() instanceof VolleyError)
                {
                    //grab the volley error from the throwable and cast it back
                    VolleyError volleyError = (VolleyError)e.getCause();
                    //now just grab the network response like normal
                    NetworkResponse networkResponse = volleyError.networkResponse;
                    try {
                        Log.d(TAG, "raw data: "+ new String(networkResponse.data));
                        JSONObject data = new JSONObject(new String(networkResponse.data));
                        Log.d(TAG, data.toString());

                        String code = data.getString("code");

                        throw (RemoteDataException) new BasicRemoteException(code);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        return null;
                    }
                }
                e.printStackTrace();
            }
            return null;
    }

    @Override
    public PoiModel setReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel removeReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }
}
