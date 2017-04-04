package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.UserModel;

/**
 * Created by Duart on 02/04/2017.
 */

public interface UserRepository {

    boolean isValidUsername(String username);

    boolean register(String email, String username, String pswd, String confPswd) throws RemoteDataException;

    boolean login(String email, String pswd);

    UserModel getUserInfo();

    boolean update(UserModel userModel);

    boolean updatePassword(String newPswd, String confPswd, String oldPswd);

}
