package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.UserModel;

public interface UserRepository {

    boolean isValidUsername(String username);

    boolean signup(String email, String username, String pswd, String confPswd) throws RemoteDataException;

    boolean signup(String userId) throws RemoteDataException;

    boolean signin(String email, String pswd);

    UserModel getUserInfo();

    boolean update(UserModel userModel);

    void signout();

    boolean updatePassword(String newPswd, String confPswd, String oldPswd);

}
