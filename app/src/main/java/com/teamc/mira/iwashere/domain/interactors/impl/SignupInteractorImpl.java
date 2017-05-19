package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.AuthInteractor;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

public class SignupInteractorImpl extends AbstractInteractor implements AuthInteractor {

    MainThread mainThread;
    AuthInteractor.Callback callback;
    UserRepository repository;
    private String userId ="";
    private String email = "";
    private String username = "";
    private String pswd = "";
    private String confPswd = "";

    public SignupInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                AuthInteractor.Callback callback, UserRepository repository,
                                String email, String username, String pswd, String confPswd) {
        super(threadExecutor, mainThread);

        this.callback = callback;
        this.repository = repository;
        this.email = email;
        this.username = username;
        this.pswd = pswd;
        this.confPswd = confPswd;
    }

    public SignupInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                AuthInteractor.Callback callback, UserRepository repository,
                                String userId) {
        super(threadExecutor, mainThread);

        this.callback = callback;
        this.repository = repository;
        this.userId = userId;
    }

    @Override
    public void run() {
        boolean result = false;
        try {
            if (!userId.isEmpty()) {
                result = repository.signUp(userId);
            } else {
                result = repository.signUp(email, username, pswd, confPswd);
            }
        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
        }

        // check if we have failed to retrieve our message
        if (!result) {
            // notify the failure on the main thread
            notifyError("network-fail", "Failed to establish a connection");
            return;
        }

        // we have retrieved our message, notify the UI on the main thread
        notifySuccess();
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
}
