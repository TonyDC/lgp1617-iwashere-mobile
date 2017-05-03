package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.PoiMapInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;

import java.util.ArrayList;

public class PoiMapInteractorImpl extends AbstractInteractor implements PoiMapInteractor {
    public final static String TAG= PoiMapInteractorImpl.class.getSimpleName();

    CallBack mCallBack;
    PoiRepository mRepository;
    double mMinLatitude;
    double mMaxLatitude;
    double mMinLongitude;
    double mMaxLongitude;

    public PoiMapInteractorImpl(Executor threadExecutor,
                                MainThread mainThread,
                                PoiMapInteractor.CallBack mCallBack,
                                PoiRepository mRepository,
                                double mMinLatitude, double mMaxLatitude, double mMinLongitude, double mMaxLongitude) {
        super(threadExecutor, mainThread);
        this.mCallBack = mCallBack;
        this.mRepository = mRepository;
        this.mMinLatitude = mMinLatitude;
        this.mMaxLatitude = mMaxLatitude;
        this.mMinLongitude = mMinLongitude;
        this.mMaxLongitude = mMaxLongitude;
    }

    @Override
    public void notifySuccess(final ArrayList<PoiModel> poiModels) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallBack.onSuccess(poiModels);
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
            ArrayList<PoiModel> poiModels;
            Log.d(TAG, "Start fetch");
            poiModels = mRepository.fetchPoisInArea(mMaxLatitude, mMinLatitude, mMaxLongitude, mMinLongitude);
            Log.d(TAG, "finished fetch");
            notifySuccess(poiModels);
        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
        }
    }
}
