package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.domain.model.UserModel;

/**
 * Created by Duart on 02/04/2017.
 */

public interface UserRepository {

    boolean isValidUsername(String username);

    String register(String email, String username, String pswd, String confPswd);

    String login(String email, String pswd);

    UserModel getUserInfo();

    boolean update(UserModel userModel);

    boolean updatePassword(String newPswd, String confPswd, String oldPswd);

}
