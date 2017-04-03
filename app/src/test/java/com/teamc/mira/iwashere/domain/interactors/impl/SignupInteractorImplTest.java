package com.teamc.mira.iwashere.domain.interactors.impl;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.UserRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.SignupInteractor;
import com.teamc.mira.iwashere.domain.repository.UserRepository;
import com.teamc.mira.iwashere.threading.MainThreadImpl;
import com.teamc.mira.iwashere.threading.TestMainThread;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * Created by Duart on 03/04/2017.
 */
public class SignupInteractorImplTest {

    private MainThread mMainThread;
    @Mock Executor mExecutor = ThreadExecutor.getInstance();
    @Mock SignupInteractor.Callback mMockedCallback;
    @Mock Context mContext;
    @Mock UserRepository mUserRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mMainThread = new TestMainThread();
    }

    @Test
    public void testWelcomeMessageFound() throws Exception {

        String email = "test@gmail.com";
        String username = "testuser";
        String password = "123456";
        String confirmPassword = "123456";



        when(mUserRepository.register(email, username, password, confirmPassword))
                .thenReturn(true);


        SignupInteractorImpl interactor = new SignupInteractorImpl(
            mExecutor,
            mMainThread,
            mMockedCallback,
            mUserRepository,
                email, username, password, confirmPassword
        );
        interactor.run();

        Mockito.verify(mUserRepository).register(email,username,password,confirmPassword);
        Mockito.verifyNoMoreInteractions(mUserRepository);
        Mockito.verify(mMockedCallback).onSuccess();
    }
}