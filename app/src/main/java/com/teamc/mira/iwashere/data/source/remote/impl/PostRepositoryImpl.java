package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.AbstractPostRepository;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;
import com.teamc.mira.iwashere.util.JsonObjectRequestWithNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT_TIME_UNIT;

/**
 * Created by Duart on 01/05/2017.
 */

public class PostRepositoryImpl extends AbstractPostRepository implements PostRepository {

    public static final String TAG = PostRepositoryImpl.class.getSimpleName();
    private static final  String API_POST_URL = ServerUrl.getUrl() + ServerUrl.API +  ServerUrl.CONTENT;
    private static final String API_POST_GET_LIKE_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.LIKE;
    private static final  String API_POST_LIKE_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.AUTH + ServerUrl.LIKE;
    private int response = -1;

    public PostRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public PostRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public PostModel fetchPost(String postId) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        String url = API_POST_URL + "/" + postId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block


            future.cancel(true);
            return new PostModel(response);
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return null;
        }
    }

    @Override
    public boolean addPostLike(PostModel post, String userId) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        final HashMap<String, Object> params = getPostLikeParams(post.getId(), userId);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_POST_LIKE_URL,
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
            post.addPostLike(post.getId(), userId);
            return getPostLike(post);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
            return false;
        }
    }

    @Override
    public boolean getPostLike(PostModel post) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        String url =  API_POST_GET_LIKE_URL + "/" + post.getId();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block

            boolean currentLike = (boolean) response.getBoolean("post_id");
            int currentRatingCount = response.getInt("ratings");
            //poi.setRating(currentRating);
            //poi.setRatingCount(currentRatingCount);

            future.cancel(true);
            return true;
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return false;
        }
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
                API_POST_URL,
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

    @Override
    public boolean updatePostLike(PostModel post, String userId, boolean liked) throws RemoteDataException {

        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        final HashMap<String, Object> params = new HashMap<>();
        params.put("postID", post.getId());

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_POST_LIKE_URL,
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
            post.addPostLike(post.getId(), userId);
            return getPostLike(post);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handleError(e);
            return false;
        }

    }

    @Override
    public ArrayList<PostModel> fetchPOIPosts(PoiModel poi) throws RemoteDataException {
        //Instantiate the RequestQueue
        RequestQueue queue = mRequestQueue;

        String url = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.POI_CONTENT + "/" + poi.getId() + "/0" + "/10";
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        return getPoiPostModelsFromRequest(future);

    }

    @Nullable
    ArrayList<PostModel> getPoiPostModelsFromRequest(RequestFuture<JSONObject> future) throws RemoteDataException {
        try {
            JSONObject response = future.get(3000, TimeUnit.MILLISECONDS); // this will block
            System.out.println(TAG + ": " + String.valueOf(response));

            ArrayList<PostModel> postModels = new ArrayList<PostModel>();
            PostModel postModel;
            JSONObject object;

            JSONArray results = response.getJSONArray("results");
            System.out.println(TAG + ": " + String.valueOf(results));

            for (int i = 0; i < results.length(); i++) {
                object = results.getJSONObject(i);
                postModel = new PostModel(object);
                postModels.add(postModel);
            }

            return postModels;
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return null;
        }
    }

}
