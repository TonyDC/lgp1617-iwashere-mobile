package com.teamc.mira.iwashere.data.source.remote.base;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.base.ErrorCodes.NETWORK_FAIL;
import static com.teamc.mira.iwashere.data.source.remote.base.ErrorCodes.UNKNOWN_ERROR;

/**
 * Created by Duart on 16/04/2017.
 */

public class AbstractRepository {

    protected RequestQueue mRequestQueue;

    public AbstractRepository(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    public AbstractRepository(Context context){
        this.mRequestQueue = Volley.newRequestQueue(context);
    }

    protected void handleError(Exception e) throws RemoteDataException {
        // check to see if the throwable is an instance of the volley error
        if(e.getCause() instanceof VolleyError)
        {
            // grab the volley error from the throwable and cast it back
            VolleyError volleyError = (VolleyError)e.getCause();
            // now just grab the network response like normal
            NetworkResponse networkResponse = volleyError.networkResponse;
            try {
                Log.d(PoiRepositoryImpl.TAG, "raw data: "+ new String(networkResponse.data));
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
