package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.AuthInteractor;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

/**
 * Created by Duart on 07/04/2017.
 */

public class SignoutInteractorImpl extends AbstractInteractor implements AuthInteractor {
    private final Callback callback;
    private final UserRepository userRepository;

    public SignoutInteractorImpl(Executor threadExecutor, MainThread mainThread, Callback callback, UserRepository userRepository) {
        super(threadExecutor, mainThread);

        this.callback = callback;
        this.userRepository = userRepository;
    }

    @Override
    public void notifyError(final String code, final String message) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onFail(code, message);
            }
        });
    }

    @Override
    public void notifySuccess() {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess();
            }
        });
   }

    @Override
    public void run() {
        userRepository.signout();
    }
}
