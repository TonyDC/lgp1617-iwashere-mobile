package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.SearchInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.model.SearchModel;
import com.teamc.mira.iwashere.domain.repository.SearchRepository;

/**
 * Created by LukášKonkoľ on 01.05.2017.
 */

public class SearchInteractorImpl extends AbstractInteractor implements SearchInteractor {
    public final static String TAG = SearchInteractorImpl.class.getSimpleName();

    SearchInteractor.CallBack mCallBack;
    SearchRepository mRepository;
    String mSearchedQuery;
    double mLatitude;
    double mLongitude;

    public SearchInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                SearchInteractor.CallBack mCallBack,
                                SearchRepository mRepository,
                                String mSearchedQuery,
                                double mLatitude, double mLongitude) {
        super(threadExecutor, mainThread);
        this.mCallBack = mCallBack;
        this.mRepository = mRepository;
        this.mSearchedQuery = mSearchedQuery;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    @Override
    public void notifySuccess(final SearchModel searchModel) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallBack.onSuccess(searchModel);
            }
        });
    }

    @Override
    public void notifyError(final String code, final String message) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallBack.onFail(message);
            }
        });
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