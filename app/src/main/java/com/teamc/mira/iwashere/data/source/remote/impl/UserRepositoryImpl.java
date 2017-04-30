package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.data.source.remote.AbstractUserRepository;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.UserModel;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT_TIME_UNIT;

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
        RequestQueue queue = mRequestQueue;

        String url = ServerUrl.getUrl();

        final HashMap<String, String> params = getRegisterParamsHashMap(email, username, password, confirmPassword);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block
            Log.d(TAG, String.valueOf(response));

            return true;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
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
