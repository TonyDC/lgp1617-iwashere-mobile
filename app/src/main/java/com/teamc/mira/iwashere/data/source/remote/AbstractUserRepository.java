package com.teamc.mira.iwashere.data.source.remote;

import android.support.annotation.NonNull;

import com.teamc.mira.iwashere.domain.repository.UserRepository;

import java.util.HashMap;

public abstract class AbstractUserRepository implements UserRepository {

    @NonNull
    protected HashMap<String, String> getRegisterParamsHashMap(String email, String username, String password, String confirmPassword) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email",email);
        params.put("name", username);
        params.put("password", password);
        params.put("confirmPassword", confirmPassword);
        return params;
    }
}