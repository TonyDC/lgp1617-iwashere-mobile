package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.UserModel;

import org.json.JSONException;

public interface UserRepository {

    boolean isValidUsername(String username);

    boolean signUp(String email, String username, String pswd, String confPswd) throws RemoteDataException, JSONException;

    boolean signUp(String userId) throws RemoteDataException;

    boolean signin(String email, String pswd);

    UserModel getUserInfo();

    boolean update(UserModel userModel);

    void signout();

    boolean updatePassword(String newPswd, String confPswd, String oldPswd);

}
