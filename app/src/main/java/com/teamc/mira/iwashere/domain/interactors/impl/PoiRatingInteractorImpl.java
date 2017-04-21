package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

public class PoiRatingInteractorImpl extends AbstractInteractor implements PoiDetailInteractor {
    CallBack callBack;
    PoiRepository repository;
    PoiModel poi = new PoiModel();
    String userId;
    int newPoiRating;

    public PoiRatingInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   CallBack callBack,
                                   PoiRepository poiRepository,
                                   PoiModel poi,
                                   String userId,
                                   int newPoiRating) {
        super(threadExecutor, mainThread);

        this.callBack = callBack;
        this.repository = poiRepository;
        this.poi = poi;
        this.userId = userId;
        this.newPoiRating = newPoiRating;
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
                callBack.onSuccess(poi);
            }
        });

    }

    @Override
    public void notifyError(String code) {
        notifyError(code, "");
    }

    @Override
    public void run() {
        PoiModel updatedPoi = null;

        try {
            updatedPoi = repository.setPoiUserRating(poi.getId(), userId, newPoiRating);
        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
            return;
        }

        poi.setRating(updatedPoi.getRating());
        poi.setRatingCount(updatedPoi.getRatingCount());
        poi.setUserRating(updatedPoi.getUserRating());

        // POI rating updated
        notifySuccess(poi);
    }
}
