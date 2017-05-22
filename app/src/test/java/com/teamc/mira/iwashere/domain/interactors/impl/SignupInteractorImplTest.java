package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.repository.remote.UserRepository;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 * Created by Duart on 03/04/2017.
 */
public class SignupInteractorImplTest extends InteractorTest {

    @Mock TemplateInteractor.CallBack             mMockedCallback;
    @Mock UserRepository                        mUserRepository;

    @Mock String email, username, password, confirmPassword;

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
        Mockito.verify(mMockedCallback).onSuccess(null);
    }
}