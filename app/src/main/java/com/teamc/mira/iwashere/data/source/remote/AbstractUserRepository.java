package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.domain.repository.remote.UserRepository;

import java.util.HashMap;

public abstract class AbstractUserRepository extends AbstractRepository implements UserRepository {

    public AbstractUserRepository(Context mContext) {
        super(mContext);
    }

    public AbstractUserRepository(RequestQueue requestQueue){
        super(requestQueue);
    }

    @NonNull
    protected HashMap<String, String> getRegisterParamsHashMap(String email, String username, String password, String confirmPassword) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email",email);
        params.put("username", username);
        params.put("password", password);
        params.put("confirmPassword", confirmPassword);
        return params;
    }

    @NonNull
    protected HashMap<String, String> getSigninParamsHashMap(String userId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", userId);
        return params;
    }
}
