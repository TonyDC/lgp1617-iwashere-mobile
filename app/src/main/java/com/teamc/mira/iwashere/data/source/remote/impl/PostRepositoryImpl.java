package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

/**
 * Created by Duart on 01/05/2017.
 */

public class PostRepositoryImpl extends AbstractRepository implements PostRepository {
    public PostRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public PostRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public boolean like(String userId, String postId, boolean liked) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean fetch(String userId, String poiId, int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean fetch(String poiId, int offset, int limit) {
        throw new UnsupportedOperationException();
    }
}
