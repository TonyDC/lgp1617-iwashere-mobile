package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.PoiContentInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractBasicInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import java.util.ArrayList;

public class PoiContentInteractorImpl extends AbstractBasicInteractor implements PoiContentInteractor {
    PoiRepository repository;
    PostRepository postRepository;
    PoiModel poi;
    String userId;
    int contentLimit;
    int contentOffset;
    int originalContentCount;

    public PoiContentInteractorImpl(Executor threadExecutor,
                                    MainThread mainThread,
                                    PoiContentInteractor.CallBack callBack,
                                    PoiRepository poiRepository,
                                    PoiModel poi,
                                    String userId,
                                    int contentOffset,
                                    int contentLimit) {
        super(threadExecutor, mainThread, callBack);

        this.repository = poiRepository;
        this.poi = poi;
        this.userId = userId;
        this.contentOffset = contentOffset;
        this.contentLimit = contentLimit;
        this.originalContentCount = poi.getContent().size();
    }

    @Override
    public void run() {
        try {
            if (!repository.fetchPoiContent(poi, userId, contentOffset, contentLimit)) {
                notifyError("520", "Error fetching POI content.");
                return;
            }

            ArrayList<PostModel> postmodels;
            postmodels = postRepository.fetchPOIPosts(poi, contentOffset, contentLimit);

        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
            return;
        }

        // TODO: 22/05/2017 Remove hardcoded for hasMoreContent : boolean. Verify if has more content
        notifySuccess(poi, true);
    }

    @Override
    public void notifySuccess(final PoiModel poi, final boolean hasMoreContent) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                ((PoiContentInteractor.CallBack) mCallBack).onSuccess(poi, hasMoreContent);
            }
        });
    }
}
