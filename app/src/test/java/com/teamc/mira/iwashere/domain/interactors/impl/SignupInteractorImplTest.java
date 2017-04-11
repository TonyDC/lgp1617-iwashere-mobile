package com.teamc.mira.iwashere.domain.interactors.impl;

import android.content.Context;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.AuthInteractor;
import com.teamc.mira.iwashere.domain.repository.UserRepository;
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

    MainThread                                  mMainThread;
    @Mock Executor                              mExecutor;
    @Mock AuthInteractor.Callback             mMockedCallback;
    @Mock Context                               mContext;
    @Mock UserRepository                        mUserRepository;

    @Mock String email, username, password, confirmPassword;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mMainThread = new TestMainThread();
    }

    @Test
    public void testOnSuccess() throws Exception {

        when(mUserRepository.signup(email, username, password, confirmPassword))
                .thenReturn(true);


        SignupInteractorImpl interactor = new SignupInteractorImpl(
            mExecutor,
            mMainThread,
            mMockedCallback,
            mUserRepository,
                email, username, password, confirmPassword
        );
        interactor.run();

        Mockito.verify(mUserRepository).signup(email,username,password,confirmPassword);
        Mockito.verifyNoMoreInteractions(mUserRepository);
        Mockito.verify(mMockedCallback).onSuccess();
    }
}