package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.teamc.mira.iwashere.data.source.local.UserRepository;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.domain.model.ContentModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
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
    protected ArrayList<URL> getMedia(JSONArray response) {
        ArrayList<URL> mediaList = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject object = null;
            try {
                object = response.getJSONObject(i);
                // TODO hadle videos as well ?
                if (object.getString("type").contains("image")) {
                    mediaList.add(new URL(object.getString("url")));
                }
            } catch (JSONException | MalformedURLException  e) {
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
