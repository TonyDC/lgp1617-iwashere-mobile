package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.teamc.mira.iwashere.data.source.local.UserRepository;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.domain.model.ContentModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by luiscosta on 5/22/17.
 */

public class AbstractPostRepository extends AbstractRepository implements PostRepository {

    public AbstractPostRepository(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public AbstractPostRepository(Context context) {
        super(context);
    }

    @NonNull
    protected HashMap<String, Object> getContentRatingParams(String poiId, String userId, int newPoiRating) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("poiID",poiId);
        params.put("rating", newPoiRating);
        return params;
    }

    @Override
    public boolean like(String userId, String postId, boolean liked) {
        return false;
    }

    @Override
    public boolean fetch(String userId, String poiId, int offset, int limit) {
        return false;
    }

    @Override
    public boolean fetch(String poiId, int offset, int limit) {
        return false;
    }

    @Override
    public boolean post(String poiId, String description, ArrayList<String> tags, Resource resource) {
        return false;
    }
}
