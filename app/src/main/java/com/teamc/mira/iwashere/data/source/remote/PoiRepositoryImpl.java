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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

// TODO: 12/04/2017 Implement functions
public class PoiRepositoryImpl extends AbstractRepository implements PoiRepository {
    public PoiRepositoryImpl(Context mContext) {
        super(mContext);
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
    public PoiModel setReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel removeReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayList<PoiModel> fetchPoisInArea(double maxLat, double minLat, double maxLong, double minLong) throws RemoteDataException {
// Instantiate the RequestQueue.
        RequestQueue queue = MySingleton.getInstance(this.mContext).getRequestQueue();

        // TODO: 03/04/2017 Extract url
        String url ="http://192.168.1.69:8080/poi/range";

        final HashMap<String, String> params = getMapRangeParameters( maxLat, minLat, maxLong, minLong);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(params), future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block
            Log.d(TAG, String.valueOf(response));

            ArrayList<PoiModel> poiModels;
            // TODO: 16/04/2017 Extract poi models from json
            throw new UnsupportedOperationException();
//            return poiModels;
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

    private HashMap<String, String> getMapRangeParameters(double maxLat, double minLat, double maxLong, double minLong) {
        throw new UnsupportedOperationException();
    }

}
