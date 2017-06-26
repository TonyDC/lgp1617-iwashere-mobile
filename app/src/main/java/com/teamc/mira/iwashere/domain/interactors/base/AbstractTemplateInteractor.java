package com.teamc.mira.iwashere.domain.interactors.base;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;

public abstract class AbstractTemplateInteractor<T> extends AbstractBasicInteractor implements TemplateInteractor<T> {
    public AbstractTemplateInteractor(Executor threadExecutor, MainThread mainThread, TemplateInteractor.CallBack callBack) {
        super(threadExecutor, mainThread, callBack);
    }

    @Override
    public void notifySuccess(final T result) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                ((TemplateInteractor.CallBack)mCallBack).onSuccess(result);
            }
        });
    }
}
