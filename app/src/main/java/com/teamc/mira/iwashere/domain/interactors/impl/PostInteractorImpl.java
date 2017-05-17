package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.PostInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

public class PostInteractorImpl extends AbstractInteractor implements PostInteractor {
    private static final String TAG = PoiDetailInteractorImpl.class.getSimpleName();
    final PostRepository mPostRepository;
    String poiId;
    String userId;

    PoiDetailInteractor.CallBack mCallBack;
    PoiRepository mPoiRepository;

    /**
     * Handle the retrieval of information about the POI with the specified id.
     * This information includes name, description, address, longitude, latitude,
     * rating, ratingCount, and userRating if userId is set.
     * The media associated with the POI is also set.
     * @param threadExecutor
     * @param mainThread
     * @param callBack
     * @param poiRepository
     * @param postRepository
     * @param poiId
     * @param userId
     */
    public PostInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   PoiDetailInteractor.CallBack callBack,
                                   PoiRepository poiRepository,
                                   PostRepository postRepository,
                                   String poiId,
                                   String userId) {
        super(threadExecutor, mainThread);

        mCallBack = callBack;
        mPoiRepository = poiRepository;
        mPostRepository = postRepository;
        this.poiId = poiId;
        this.userId = userId;

    }

    @Override
    public void notifySuccess(){}

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
    public void run() {

    }
}
