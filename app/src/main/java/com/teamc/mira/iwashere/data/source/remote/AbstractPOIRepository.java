package com.teamc.mira.iwashere.data.source.remote;

import android.support.annotation.NonNull;

import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.UserRepository;

import org.json.JSONObject;

import java.util.HashMap;

public abstract class AbstractPOIRepository implements PoiRepository {

    @NonNull
    protected HashMap<String, String> getPostRatingParams(String poiId, String userId, int newPoiRating) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("poiId",poiId);
        params.put("userId", userId);
        params.put("rating", newPoiRating + "");
        return params;
    }

    @NonNull
    protected PoiModel getPoiDetails(JSONObject response) {
        throw new UnsupportedOperationException();
    }
}
