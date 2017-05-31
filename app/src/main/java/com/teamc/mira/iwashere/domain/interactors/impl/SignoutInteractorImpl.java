package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.repository.remote.UserRepository;

/**
 * Created by Duart on 07/04/2017.
 */

public class SignoutInteractorImpl extends AbstractTemplateInteractor{
    private final UserRepository userRepository;

    public SignoutInteractorImpl(Executor threadExecutor, MainThread mainThread, TemplateInteractor.CallBack callBack, UserRepository userRepository) {
        super(threadExecutor, mainThread, callBack);

        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        userRepository.signout();
        notifySuccess(null);
    }
}
