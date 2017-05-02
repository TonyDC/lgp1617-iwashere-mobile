package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.PoiContentInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

public class PoiContentInteractorImpl extends AbstractInteractor implements PoiContentInteractor {
    CallBack callBack;
    PoiRepository repository;
    PoiModel poi;
    String userId;
    int contentLimit;
    int contentOffset;
    int originalContentCount;

    public PoiContentInteractorImpl(Executor threadExecutor,
                                    MainThread mainThread,
                                    CallBack callBack,
                                    PoiRepository poiRepository,
                                    PoiModel poi,
                                    String userId,
                                    int contentOffset,
                                    int contentLimit) {
        super(threadExecutor, mainThread);

        this.callBack = callBack;
        this.repository = poiRepository;
        this.poi = poi;
        this.userId = userId;
        this.contentOffset = contentOffset;
        this.contentLimit = contentLimit;
        this.originalContentCount = poi.getContent().size();
    }

    @Override
    public void notifyError(final String code, final String message) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                if (code.equals("network-fail")) {
                    callBack.onNetworkFail();
                }else {
                    callBack.onError(code, message);
                }
            }
        });
    }

    @Override
    public void notifySuccess(final PoiModel poi) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(poi, poi.getContent().size() >= originalContentCount);
            }
        });

    }

    @Override
    public void notifyError(String code) {
        notifyError(code, "");
    }

    @Override
    public void run() {
        try {
            if (!repository.fetchPoiContent(poi, userId, contentOffset, contentLimit)) {
                notifyError("520", "Error fetching POI content.");
                return;
            }
        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
            return;
        }

        notifySuccess(poi);
    }
}
