package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.SearchModel;
import com.teamc.mira.iwashere.domain.repository.SearchRepository;

/**
 * Created by LukášKonkoľ on 01.05.2017.
 */

public class SearchInteractorImpl extends AbstractTemplateInteractor{
    public final static String TAG = SearchInteractorImpl.class.getSimpleName();

    SearchRepository mRepository;
    String mSearchedQuery;
    double mLatitude;
    double mLongitude;

    public SearchInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                TemplateInteractor.CallBack callBack,
                                SearchRepository mRepository,
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
            SearchModel searchModel;
            Log.d(TAG, "Start search");
            searchModel = mRepository.search(mSearchedQuery, mLatitude, mLongitude);
            Log.d(TAG, "Finished search");
            notifySuccess(searchModel);
        } catch (RemoteDataException e) {
            notifyError(e.getCode(), e.getErrorMessage());
        }
    }
}