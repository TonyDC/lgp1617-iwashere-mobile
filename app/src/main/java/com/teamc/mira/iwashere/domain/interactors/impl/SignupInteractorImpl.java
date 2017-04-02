package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.SignupInteractor;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

/**
 * Created by Duart on 02/04/2017.
 */

public class SignupInteractorImpl extends AbstractInteractor implements SignupInteractor {

    MainThread mainThread;
    SignupInteractor.Callback callback;
    UserRepository repository;
    private final String email;
    private final String username;
    private final String pswd;
    private final String confPswd;


    public SignupInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                SignupInteractor.Callback callback, UserRepository repository,
                                String email, String username, String pswd, String confPswd) {
        super(threadExecutor, mainThread);

        this.callback = callback;
        this.repository = repository;
        this.email = email;
        this.username = username;
        this.pswd = pswd;
        this.confPswd = confPswd;
    }

    @Override
    public void run() {
        // retrieve the message
        final String message = repository.register(email,username,pswd,confPswd);

        // check if we have failed to retrieve our message
        if (message == null || message.length() == 0) {

            // notify the failure on the main thread
            notifyError("network-failed", "Failed to establish a connection");
            return;
        }

        // we have retrieved our message, notify the UI on the main thread
        notifySuccess();
    }

    @Override
    public void notifyError(String code, String message) {

    }

    @Override
    public void notifySuccess() {

    }
}
