package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.AbstractPostRepository;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT_TIME_UNIT;

/**
 * Created by Duart on 01/05/2017.
 */

public class PostRepositoryImpl extends AbstractRepository implements PostRepository {
   private static final  String API_POST = ServerUrl.getUrl() + ServerUrl.API +  ServerUrl.CONTENT;
   private static final  String API_CONTENT_POST_RATING_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.AUTH + ServerUrl.RATING;
    private int response = -1;

    public PostRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public PostRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public boolean like(String userId, String postId, boolean liked) throws RemoteDataException {
        /// Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        final HashMap<String, Object> params = getContentRatingParams(poi.getId(), userId, newPoiRating);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_CONTENT_POST_RATING_URL,
                new JSONObject(params),
                future, future){
            @Override
            public HashMap<String, String> getHeaders() {
                return PostRepositoryImpl.this.getHeaders();
            }
        };

        queue.add(request);

        try {
            future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block
            poi.setUserRating(newPoiRating);
            return fetchPoiRating(poi);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean fetch(String userId, String poiId, int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean fetch(String poiId, int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean post(String poiId, String description, ArrayList<String> tags, Resource resource) {

        RequestQueue queue = mRequestQueue;

        final HashMap<String, Object> params = new HashMap<>();
        params.put("poiId", poiId);
        params.put("description", description);
        params.put("tags", tags);
        params.put("resource", resource);


        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_POST,
                new JSONObject(params),
                future, future) {
            @Override
            public HashMap<String, String> getHeaders() {
                return PostRepositoryImpl.this.getHeaders();
            }
        };

        queue.add(request);

        try {
            JSONObject response = future.get(ServerUrl.TIMEOUT, ServerUrl.TIMEOUT_TIME_UNIT); // this will block
        }catch (InterruptedException | ExecutionException | TimeoutException e){
            e.printStackTrace();
            return false;
        }



    return true;
    }

}
