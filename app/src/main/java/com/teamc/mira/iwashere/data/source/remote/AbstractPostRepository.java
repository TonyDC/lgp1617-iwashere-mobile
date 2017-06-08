package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import java.util.HashMap;

/**
 * Created by luiscosta on 5/22/17.
 */

public abstract class AbstractPostRepository extends AbstractRepository implements PostRepository {

    public AbstractPostRepository(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public AbstractPostRepository(Context context) {
        super(context);
    }

    @NonNull
    protected HashMap<String, Object> getPostLikeParams(String postId, String userId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("postID",postId);
        return params;
    }
}