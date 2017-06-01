package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import java.util.ArrayList;


public class PostInteractorImpl extends AbstractTemplateInteractor<PostModel> {
    PostRepository repository;
    TemplateInteractor.CallBack callBack;
    String poiId;
    String description;
    ArrayList<String> tags;
    PostModel post;
    Resource resource;


    public PostInteractorImpl(Executor threadExecutor,
                              MainThread mainThread,
                              TemplateInteractor.CallBack callBack,
                              PostRepository postRepository,
                              PostModel post,
                              String poiId,
                              String description,
                              ArrayList<String> tags,
                              Resource resource) {
        super(threadExecutor, mainThread, callBack);

        this.callBack = callBack;
        repository = postRepository;
        this.poiId = poiId;
        this.description = description;
        this.resource = resource;
        this.tags = tags;
    }

    @Override
    public void run() {
        repository.post(poiId, description, tags, resource);
        notifySuccess(post);
    }
}
