package com.teamc.mira.iwashere.domain.interactors.impl;

import android.util.Log;

import com.teamc.mira.iwashere.data.source.local.UserRepository;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.PostInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractInteractor;

import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import java.util.ArrayList;


public class PostInteractorImpl extends AbstractInteractor implements PostInteractor {
    PostRepository repository;
    CallBack callBack;
    String poiId;
    String userId;
    String description;
    ArrayList<String> tags;
    PostModel post;
    Resource resource;


    public PostInteractorImpl(Executor threadExecutor,
                              MainThread mainThread,
                              CallBack callBack,
                              PostRepository postRepository,
                              PostModel post,
                              String userId,
                              String poiId,
                              String description,
                              Resource resource,
                              ArrayList<String> tags) {
        super(threadExecutor, mainThread);

        this.callBack = callBack;
        repository = postRepository;
        this.poiId = poiId;
        this.userId = userId;
        this.description = description;
        this.resource = resource;
    }

    @Override
    public void notifyError(final String code, final String message) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                if (code.equals("network-fail")) {
                    callBack.onNetworkFail();
                } else {
                    callBack.onError(code, message);
                }
            }
        });
    }

    @Override
    public void notifySuccess(final PostModel post) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(post);
            }
        });

    }

    @Override
    public void notifyError(String code) {
        notifyError(code, "");
    }

    @Override
    public void run() {
        repository.post(userId, poiId, description, tags, resource);
        notifySuccess(post);
    }
}
