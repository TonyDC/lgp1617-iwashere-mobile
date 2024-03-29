package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.repository.remote.UserRepository;

import org.json.JSONException;

public class SignupInteractorImpl extends AbstractTemplateInteractor{
    TemplateInteractor.CallBack callBack;
    UserRepository repository;
    private String userId = "";
    private String email = "";
    private String username = "";
    private String pswd = "";
    private String confPswd = "";

    public SignupInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                TemplateInteractor.CallBack callBack, UserRepository repository,
                                String email, String username, String pswd, String confPswd) {
        super(threadExecutor, mainThread, callBack);

        this.repository = repository;
        this.email = email;
        this.username = username;
        this.pswd = pswd;
        this.confPswd = confPswd;
    }

    public SignupInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                TemplateInteractor.CallBack callBack, UserRepository repository,
                                String userId) {
        super(threadExecutor, mainThread, callBack);
        this.repository = repository;
        this.userId = userId;
    }

    @Override
    public void run() {
        boolean result;
        try {
            if (!userId.isEmpty()) {
                result = repository.signUp(userId);
            } else {
                result = repository.signUp(email, username, pswd, confPswd);
            }
        } catch (RemoteDataException e) {
            notifyError(e.getCode(), e.getErrorMessage());
            return;
        } catch (JSONException e) {
            notifyError("JSONException", e.getMessage());
            return;
        }
        // we have retrieved our message, notify the UI on the main thread
        if (!result) {
            notifyError("network-fail", "Failed to establish a connection");
        } else {
            notifySuccess(null);
        }
    }
}