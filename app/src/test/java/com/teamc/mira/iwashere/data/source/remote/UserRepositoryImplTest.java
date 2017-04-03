package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * Created by Duart on 03/04/2017.
 */
public class UserRepositoryImplTest {
    @Mock
    Context mMockContext;

    @Test
    public void register() throws Exception {
        UserRepositoryImpl userRepository = new UserRepositoryImpl(mMockContext);

        String email = "test@gmail.com";
        String username = "username";
        String password = "123456";
        String confirmPassword = "123456";

        assertTrue(userRepository.register(email, username, password, confirmPassword));
    }

}