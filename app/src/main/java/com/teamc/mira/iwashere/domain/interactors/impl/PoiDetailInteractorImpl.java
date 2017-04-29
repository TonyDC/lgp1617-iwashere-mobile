package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

public class PoiDetailInteractorImpl extends AbstractInteractor implements PoiDetailInteractor {
    private static final String TAG = PoiDetailInteractorImpl.class.getSimpleName();
    String poiId;
    String userId;

    PoiDetailInteractor.CallBack mCallBack;
    PoiRepository mRepository;

    /**
     * Handle the retrieval of information about the POI with the specified id.
     * This information includes name, description, address, longitude, latitude,
     * rating, ratingCount, and userRating if userId is set.
     * The media associated with the POI is also set.
     * @param threadExecutor
     * @param mainThread
     * @param callBack
     * @param poiRepository
     * @param poiId
     * @param userId
     */
    public PoiDetailInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   PoiDetailInteractor.CallBack callBack,
                                   PoiRepository poiRepository,
                                   String poiId,
                                   String userId) {
        super(threadExecutor, mainThread);

        mCallBack = callBack;
        mRepository = poiRepository;
        this.poiId = poiId;
        this.userId = userId;

    }

    @Override
    public void notifyError(final String code, final String message) {
        Log.d(TAG, "notifyError start");
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                // TODO: 12/04/2017 extract hardcoded fails
                Log.d(TAG, "notifyError callback start");
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
            Log.d(TAG, "Start fetching poi");
            poi = mRepository.fetchPoi(poiId);
            Log.d(TAG, "Start fetching poi rating");
            if (!mRepository.fetchPoiRating(poi)) {
                notifyError("520", "Error fetching POI rating.");
                return;
            }

            Log.d(TAG, "Start fetching poi media");
            if(!mRepository.fetchPoiMedia(poi)) {
                notifyError("520", "Error fetching POI's media.");
                return;
            }

            Log.d(TAG, "Start fetching poi User Rating");
            // TODO: 29/04/2017 Can't fix issue where the fetchPoiUserRating function blocks indefinitely and doesn't timeout
            /*if (userId != null && !mRepository.fetchPoiUserRating(poi, userId)) {
                notifyError("520", "Error fetching POI user's rating.");
                return;
            }*/

            Log.d(TAG, "Start nofitySuccess");
            notifySuccess(poi);
        }catch (RemoteDataException ex){
            notifyError(ex.getCode(), ex.getErrorMessage());
        }

    }
}
