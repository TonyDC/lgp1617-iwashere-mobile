package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.UserModel;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.android.volley.Request.Method.POST;
import static com.teamc.mira.iwashere.data.source.remote.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.ServerUrl.TIMEOUT_TIME_UNIT;

public class UserRepositoryImpl extends AbstractUserRepository implements UserRepository {

    private static final String API_REGISTER_BY_PROVIDER_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.USER + ServerUrl.UNAUTH + ServerUrl.REGISTER_BY_PROVIDER;
    private static final String API_REGISTER_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.USER + ServerUrl.UNAUTH + ServerUrl.REGISTER;

    public UserRepositoryImpl(Context mContext) {
        super(mContext);
    }

    public UserRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public static final String TAG = UserRepositoryImpl.class.getSimpleName();

    @Override
    public boolean isValidUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean signUp(String email, String username, String password, String confirmPassword) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        final HashMap<String, String> params = getRegisterParamsHashMap(email, username, password, confirmPassword);

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(POST, API_REGISTER_URL, future, future) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);

        return getSignUpByProviderResult(future);
    }

    @Override
    public boolean signUp(String userId) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        final HashMap<String, String> params = getSigninParamsHashMap(userId);

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(POST, API_REGISTER_BY_PROVIDER_URL, future, future) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);

        return getSignUpByProviderResult(future);
    }

    private boolean getSignUpByProviderResult(RequestFuture<String> future) throws RemoteDataException {
        try {
            String response = String.valueOf(future.get(TIMEOUT, TIMEOUT_TIME_UNIT)); // this will block
            Log.d(TAG, String.valueOf(response));
            future.cancel(true);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            //check to see if the throwable in an instance of the volley error
            if (e.getCause() instanceof VolleyError) {
                //grab the volley error from the throwable and cast it back
                VolleyError volleyError = (VolleyError) e.getCause();
                //now just grab the network response like normal
                NetworkResponse networkResponse = volleyError.networkResponse;
                try {
                    Log.d(TAG, "raw data: " + new String(networkResponse.data));
                    JSONObject data = new JSONObject(new String(networkResponse.data));
                    Log.d(TAG, data.toString());

                    String code = data.getString("code");

                    throw new BasicRemoteException(code);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    throw new BasicRemoteException("unknown-error");
                }
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BasicRemoteException("unknown-error");
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

    private void handleError(Exception e) throws RemoteDataException {
        // check to see if the throwable is an instance of the volley error
        if (e.getCause() instanceof VolleyError) {
            // grab the volley error from the throwable and cast it back
            VolleyError volleyError = (VolleyError) e.getCause();
            // now just grab the network response like normal
            NetworkResponse networkResponse = volleyError.networkResponse;
            try {
                Log.d(TAG, "raw data: " + new String(networkResponse.data));
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
