package com.teamc.mira.iwashere.data.source.remote;

import com.teamc.mira.iwashere.domain.model.UserModel;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

/**
 * Created by Duart on 02/04/2017.
 */

public class UserRepositoryImpl implements UserRepository {
    @Override
    public boolean isValidUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String register(String email, String username, String pswd, String confPswd) {
        return null;
    }

    @Override
    public String login(String email, String pswd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserModel getUserInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updatePassword(String newPswd, String confPswd, String oldPswd) {
        throw new UnsupportedOperationException();
    }
}
