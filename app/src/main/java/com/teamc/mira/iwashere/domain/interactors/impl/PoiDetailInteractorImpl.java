package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

public class PoiDetailInteractorImpl extends AbstractInteractor implements PoiDetailInteractor {
    String mId;
    PoiDetailInteractor.CallBack mCallBack;
    PoiRepository mRepository;

    public PoiDetailInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   PoiDetailInteractor.CallBack callBack,
                                   PoiRepository poiRepository,
                                   String id) {
        super(threadExecutor, mainThread);

        mCallBack = callBack;
        mRepository = poiRepository;
        mId = id;
    }

    public PoiDetailInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   PoiDetailInteractor.CallBack callBack,
                                   PoiRepository poiRepository,
                                   PoiModel poi) {
        this(threadExecutor, mainThread, callBack, poiRepository, poi.getId());
    }

    @Override
    public void notifyError(final String code, final String message) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                // TODO: 12/04/2017 extract hardcoded fails
                if (code.equals("network-fail")) {
                    mCallBack.onNetworkFail();
                }else {
                    mCallBack.onError(code, message);
                }
            }
        });
    }

    @Override
    public void notifySuccess(final PoiModel poi) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallBack.onSuccess(poi);
            }
        });

    }

    @Override
    public void notifyError(String code) {
        notifyError(code, "");
    }

    @Override
    public void run() {
        PoiModel poi;
        try {
            poi = mRepository.fetchPoi(mId);
            notifySuccess(poi);
        }catch (RemoteDataException ex){
            notifyError(ex.getCode(), ex.getErrorMessage());
        }

    }
}
