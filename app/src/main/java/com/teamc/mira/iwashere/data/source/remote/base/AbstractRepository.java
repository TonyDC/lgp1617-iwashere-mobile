package com.teamc.mira.iwashere.data.source.remote.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.teamc.mira.iwashere.data.source.local.UserRepository;
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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

    @NonNull
    protected HashMap<String, String> getHeaders() {
        //String token = UserRepository.getInstance().getToken();
        HashMap<String, String> params = new HashMap<>();
        params.put("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsImtpZCI6IjBmOWVmYTVjZWVhNTVjZWM1OTk2MzAzZGE0Y2Y4MmJlNmJlYjdmZmIifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vaXdhc2hlcmUtbW9iaWxlIiwibmFtZSI6ImJiYmJiIiwiYXVkIjoiaXdhc2hlcmUtbW9iaWxlIiwiYXV0aF90aW1lIjoxNDk2MTgwMTgxLCJ1c2VyX2lkIjoiRWFJUWpUQUY1TlFBa3MwVGxpS3l2ZUx3OHZDMyIsInN1YiI6IkVhSVFqVEFGNU5RQWtzMFRsaUt5dmVMdzh2QzMiLCJpYXQiOjE0OTYxODMzOTgsImV4cCI6MTQ5NjE4Njk5OCwiZW1haWwiOiJiQGIucHQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsiYkBiLnB0Il19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.NZZj80scLUSdckFAOpOKaz8Gv1ZRiObKpv3RC3RklVJKyhnZQC_TQjOZHj9lPdjgCp10vibKZIy5M12NzaXK52ETzCogTN817RORjTYk9hGLiq-qHbHNb4we4CvkE5mQVdfAJGOLBSMvQQ_cL6KdS55B0a5X_1BX2AtWtUEsKijzAThAp7x21BbUL4H3zZlQGoRkdL53A6-XupwDPT0KAQ59GHyQQViEFRnbT7FBC0Qh0a2Umzfn73QvxRLzdfYQZay6xtuxm8cm-cW51q4rGwGy40RYeEbot0XRjm5-3qPnLNLawLo3vwkakxykMESkY7_gruJ6lg_E74JZeTi-Fg");
        //"Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBmOWVmYTVjZWVhNTVjZWM1OTk2MzAzZGE0Y2Y4MmJlNmJlYjdmZmIifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vaXdhc2hlcmUtbW9iaWxlIiwibmFtZSI6ImJiYmJiIiwiYXVkIjoiaXdhc2hlcmUtbW9iaWxlIiwiYXV0aF90aW1lIjoxNDk2MTgwMTU4LCJ1c2VyX2lkIjoiRWFJUWpUQUY1TlFBa3MwVGxpS3l2ZUx3OHZDMyIsInN1YiI6IkVhSVFqVEFGNU5RQWtzMFRsaUt5dmVMdzh2QzMiLCJpYXQiOjE0OTYxODAxNTgsImV4cCI6MTQ5NjE4Mzc1OCwiZW1haWwiOiJiQGIucHQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsiYkBiLnB0Il19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.C3wpdn36kcfDp-GRYyJDudqlKYcdHJkLbs-kea7XELCnHciSQ27bNdXPcVeDOpoMCQv7po4MG33WJRINdvOnO0kDSAwB8KftAWx86G7RZCxXwEGowX9NWiBCbcM4h5GvUNCv4oFlgncjbheKNNKJlQDNZOxxqplbLYUJR4-8ZxIKo6yimgQJohb5D9oWX4dRfXVcXSQrqY5jMQXMj_Juo15PnoMPwJO8AQbmL66hSUNi1B34UxnkhMQb1vxACrzJLAGCng9e-VA1ZqQzLxBFJ7oWbF9fA_TKJMI6NgvfXq-7pnJAWwQPQYCr00yjxYD0pod-sFqR6lqpP9R62fVhmw"
        params.put("Content-Type", "application/json;charset=UTF-8");
        params.put("Accept", "application/json");
        return params;
    }
}
