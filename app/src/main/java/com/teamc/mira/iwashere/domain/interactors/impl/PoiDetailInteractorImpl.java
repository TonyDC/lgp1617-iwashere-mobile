package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import java.util.ArrayList;

public class PoiDetailInteractorImpl extends AbstractTemplateInteractor<PoiModel> {
    private static final String TAG = PoiDetailInteractorImpl.class.getSimpleName();
    final PostRepository mPostRepository;
    String poiId;
    String userId;

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
    public PoiDetailInteractorImpl(Executor threadExecutor,
                                   MainThread mainThread,
                                   TemplateInteractor.CallBack callBack,
                                   PoiRepository poiRepository,
                                   PostRepository postRepository,
                                   String poiId,
                                   String userId) {
        super(threadExecutor, mainThread, callBack);

        mPoiRepository = poiRepository;
        mPostRepository = postRepository;
        this.poiId = poiId;
        this.userId = userId;

    }

    @Override
    public void run() {
        PoiModel poi;
        try {
            Log.d(TAG, "Start fetching poi");
            poi = mPoiRepository.fetchPoi(poiId);
            Log.d(TAG, "Start fetching poi rating");
            if (!mPoiRepository.fetchPoiRating(poi)) {
                notifyError("520", "Error fetching POI rating.");
                return;
            }

            Log.d(TAG, "Start fetching poi media");
            if(!mPoiRepository.fetchPoiMedia(poi)) {
                notifyError("520", "Error fetching POI's media.");
                return;
            }

            Log.d(TAG, "Start fetching poi User Rating");
            // TODO: 29/04/2017 Can't fix issue where the fetchPoiUserRating function blocks indefinitely and doesn't timeout
            if (userId != null && !mPoiRepository.fetchPoiUserRating(poi, userId)) {
                notifyError("520", "Error fetching POI user's rating.");
                return;
            }

            Log.d(TAG, "Start nofitySuccess");
            notifySuccess(poi);
        }catch (RemoteDataException ex){
            notifyError(ex.getCode(), ex.getErrorMessage());
        }

    }
}
