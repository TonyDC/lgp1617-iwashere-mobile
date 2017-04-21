package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.UserModel;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class UserRepositoryImpl extends AbstractUserRepository implements UserRepository {

    public UserRepositoryImpl(Context mContext) {
        super(mContext);
    }

    public UserRepositoryImpl(RequestQueue requestQueue){
        super(requestQueue);
    }

    public static final String TAG = UserRepositoryImpl.class.getSimpleName();
    @Override
    public boolean isValidUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean signup(String email, String username, String password, String confirmPassword) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        // TODO: 03/04/2017 Extract url
        String url ="http://192.168.1.69:8080/api/signup";

        final HashMap<String, String> params = getRegisterParamsHashMap(email, username, password, confirmPassword);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block
            Log.d(TAG, String.valueOf(response));

            return true;
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
                    return false;
                }
            }
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean signin(String email, String pswd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserModel getUserInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update(UserModel userModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void signout() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean updatePassword(String newPswd, String confPswd, String oldPswd) {
        throw new UnsupportedOperationException();
    }
}
