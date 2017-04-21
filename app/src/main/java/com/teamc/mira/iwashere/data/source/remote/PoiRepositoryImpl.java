package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

// TODO: 12/04/2017 Implement functions
public class PoiRepositoryImpl extends AbstractRepository implements PoiRepository {
    public PoiRepositoryImpl(Context mContext) {
        super(mContext);
    }

    public PoiRepositoryImpl(RequestQueue requestQueue){
        super(requestQueue);
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
        RequestQueue queue = mRequestQueue;

        // TODO: 03/04/2017 Extract url
        String url ="http://172.30.5.114:8080/poi/range";

        url += minLat+"/"+maxLat+"/"+minLong+"/"+maxLong;

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        return getPoiModelsFromRequest(future);
    }

    @Nullable
    ArrayList<PoiModel> getPoiModelsFromRequest(RequestFuture<JSONArray> future) throws RemoteDataException {
        try {
            JSONArray response = future.get(1000, TimeUnit.MILLISECONDS); // this will block
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
                    throw (RemoteDataException) new BasicRemoteException("unknown-error");
                }
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw (RemoteDataException) new BasicRemoteException("unknown-error");
        }
        return null;
    }

}
