package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.domain.model.ContentModel;
import com.teamc.mira.iwashere.domain.model.util.BasicResource;
import com.teamc.mira.iwashere.domain.model.util.ImageResource;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.model.util.ResourceFactory;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractPoiRepository extends AbstractRepository implements PoiRepository {

    public AbstractPoiRepository(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public AbstractPoiRepository(Context context) {
        super(context);
    }

    @NonNull
    protected HashMap<String, Object> getPostRatingParams(String poiId, String userId, int newPoiRating) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("poiID",poiId);
        params.put("rating", newPoiRating);
        return params;
    }

    @NonNull
    protected ArrayList<Resource> getMedia(JSONArray response) {
        ArrayList<Resource> mediaList = new ArrayList<>();


        for (int i = 0; i < response.length(); i++) {
            JSONObject object = null;
            try {
                object = response.getJSONObject(i);
                mediaList.add(ResourceFactory.getResource(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mediaList;
    }

    @NonNull
    protected ArrayList<ContentModel> getContent(JSONArray response) {
        ArrayList<ContentModel> contentList = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject object = null;
            try {
                object = response.getJSONObject(i);
                contentList.add(new ContentModel(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return contentList;
    }

}
