package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;

import java.util.ArrayList;

public class PoiSearchInteractorImpl extends AbstractTemplateInteractor{
    public final static String TAG = PoiMapInteractorImpl.class.getSimpleName();

    PoiRepository mRepository;
    String mSearchedQuery;
    double mLatitude;
    double mLongitude;

    public PoiSearchInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   TemplateInteractor.CallBack callBack,
                                   PoiRepository mRepository,
                                   String mSearchedQuery,
                                   double mLatitude, double mLongitude) {
        super(threadExecutor, mainThread, callBack);
        this.mRepository = mRepository;
        this.mSearchedQuery = mSearchedQuery;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    @Override
    public void run() {
        try {
            ArrayList<PoiModel> poiModels;
            Log.d(TAG, "POI Start search");
            poiModels = mRepository.searchPois(mSearchedQuery, mLatitude, mLongitude);
            Log.d(TAG, "POI finished search");
            notifySuccess(poiModels);
        } catch (RemoteDataException e) {
            notifyError(e.getCode(), e.getErrorMessage());
        }
    }
}