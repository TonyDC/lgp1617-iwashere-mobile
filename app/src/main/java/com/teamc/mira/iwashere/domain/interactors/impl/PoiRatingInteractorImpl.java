package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;

public class PoiRatingInteractorImpl extends AbstractTemplateInteractor {
    TemplateInteractor.CallBack callBack;
    PoiRepository repository;
    PoiModel poi;
    String userId;
    int newPoiRating;

    public PoiRatingInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   TemplateInteractor.CallBack callBack,
                                   PoiRepository poiRepository,
                                   PoiModel poi,
                                   String userId,
                                   int newPoiRating) {
        super(threadExecutor, mainThread, callBack);

        this.callBack = callBack;
        this.repository = poiRepository;
        this.poi = poi;
        this.userId = userId;
        this.newPoiRating = newPoiRating;
    }

    @Override
    public void run() {

        try {
            repository.setPoiUserRating(poi, userId, newPoiRating);
        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
            return;
        }
        
        // POI rating updated
        notifySuccess(poi);
    }
}
