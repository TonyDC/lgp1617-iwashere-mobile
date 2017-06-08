package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import java.io.File;
import java.util.ArrayList;

import static com.teamc.mira.iwashere.data.source.remote.base.ErrorCodes.UNKNOWN_ERROR;


public class PostInteractorImpl extends AbstractTemplateInteractor{
    PostRepository repository;
    String poiId;
    String description;
    ArrayList<String> tags;
    PostModel post;
    File resource;


    public PostInteractorImpl(Executor threadExecutor,
                              MainThread mainThread,
                              TemplateInteractor.CallBack callBack,
                              PostRepository postRepository,
                              PostModel post,
                              String poiId,
                              String description,
                              ArrayList<String> tags,
                              File resource) {
        super(threadExecutor, mainThread, callBack);

        repository = postRepository;
        this.poiId = poiId;
        this.description = description;
        this.resource = resource;
        this.tags = tags;
    }

    @Override
    public void run() {
        try{
            boolean success = repository.post(poiId, description, tags, resource);

            if (success) {
                notifySuccess(post);
            } else {
                notifyError(UNKNOWN_ERROR, "");
            }
        } catch (RemoteDataException e) {
            notifyError(e.getCode(), e.getErrorMessage());
        }
    }
}