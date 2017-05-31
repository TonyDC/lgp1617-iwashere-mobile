package com.teamc.mira.iwashere.domain.interactors.base;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;

/**
 * Created by Duart on 22/05/2017.
 */

public abstract class AbstractBasicInteractor extends AbstractInteractor implements BasicInteractor {
    protected BasicInteractor.CallBack mCallBack;
    public AbstractBasicInteractor(Executor threadExecutor, MainThread mainThread, BasicInteractor.CallBack callBack) {
        super(threadExecutor, mainThread);
        mCallBack = callBack;
    }

    @Override
    public void notifyError(final String code, final String message) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                if (code.equals("network-fail")) {
                    mCallBack.onNetworkError();
                }else {
                    mCallBack.onError(code, message);
                }
            }
        })
;    }
}
