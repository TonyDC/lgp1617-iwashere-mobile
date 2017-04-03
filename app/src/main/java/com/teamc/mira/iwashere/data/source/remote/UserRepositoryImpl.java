package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.teamc.mira.iwashere.domain.model.UserModel;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by Duart on 02/04/2017.
 */

public class UserRepositoryImpl implements UserRepository {

    Context mContext;

    public UserRepositoryImpl(Context mContext) {
        this.mContext = mContext;
    }

    public static final String TAG = UserRepositoryImpl.class.getSimpleName();
    @Override
    public boolean isValidUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean register(String email, String username, String password, String confirmPassword) {
        // Instantiate the RequestQueue.
        RequestQueue queue = MySingleton.getInstance(mContext).getRequestQueue();
        String url ="http://localhost:8080/api/request";

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("email",email);
        params.put("username", username);
        params.put("password", password);
        params.put("confirmPassword", confirmPassword);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params), future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(); // this will block
            Log.d(TAG, String.valueOf(response));

            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean login(String email, String pswd) {
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
    public boolean updatePassword(String newPswd, String confPswd, String oldPswd) {
        throw new UnsupportedOperationException();
    }
}
