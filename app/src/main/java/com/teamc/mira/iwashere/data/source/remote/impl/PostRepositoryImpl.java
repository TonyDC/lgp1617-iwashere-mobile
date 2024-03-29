package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.local.UserRepository;
import com.teamc.mira.iwashere.data.source.remote.AbstractPostRepository;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;
import com.teamc.mira.iwashere.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.teamc.mira.iwashere.data.source.remote.base.ErrorCodes.UNKNOWN_ERROR;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT_TIME_UNIT;

/**
 * Created by Duart on 01/05/2017.
 */

public class PostRepositoryImpl extends AbstractPostRepository implements PostRepository {

    public static final String TAG = PostRepositoryImpl.class.getSimpleName();
    private static final String API_POST_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.AUTH;
    private static final String API_POST_GET_LIKE_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.LIKE;
    private static final String API_POST_LIKE_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.CONTENT + ServerUrl.AUTH + ServerUrl.LIKE;
    private final Context mContext;
    private int response = -1;

    public PostRepositoryImpl(Context context) {
        super(context);
        this.mContext = context;
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
                com.android.volley.Request.Method.POST,
                API_POST_LIKE_URL,
                new JSONObject(params),
                future, future) {
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

        String url = API_POST_GET_LIKE_URL + "/" + post.getId();
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

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }



    @Override
    public boolean post(String poiId, String description, ArrayList<String> tags, File resource) throws BasicRemoteException {

        String extension = FileUtil.getFileExtension(resource.getAbsolutePath());
        String media = "image/";
        if (extension.equals("jpg") ||extension.equals("png")) {
            media += extension;
        } else if (extension.equals("mp4")) {
            media = "video/" + extension;
        }

        OkHttpClient client = new OkHttpClient();

        try {
            Log.d(TAG, "Start building body");
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("poiID", "1")
                    .addFormDataPart("description", description)
                    .addFormDataPart("postFiles", resource.getName(),
                            RequestBody.create(MediaType.parse(media), resource));
            addTagsToBody(bodyBuilder, tags);
            Log.d(TAG, "BodyBuilder finished");
            RequestBody requestBody = bodyBuilder.build();
            Log.d(TAG, "Finished building Body");
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(API_POST_URL)
                    .method("POST", requestBody)
                    .addHeader("Authorization", "Bearer " + UserRepository.getInstance().getToken())

                    .build();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final boolean[] successful = new boolean[1];
            successful[0] = true;
            Log.d(TAG, "Started call");
            Looper.prepare();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "okhttp3 onFailure " + e);
                    e.printStackTrace();
//                    Toast.makeText(mContext, mContext.getString(R.string.post_upload_failed), Toast.LENGTH_SHORT).show();
                    successful[0] = false;
                    countDownLatch.countDown();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "okhttp3 onResponse " + response.isSuccessful());
                    System.out.println("okhttp3 onResponse " + response.isSuccessful());
                    if (!response.isSuccessful()) {
                        // Handle Error
//                        Toast.makeText(mContext, mContext.getString(R.string.post_upload_failed), Toast.LENGTH_SHORT).show();
                        successful[0] = false;
                    } else {
                        // Upload successful
//                        Toast.makeText(mContext, mContext.getString(R.string.post_uploaded), Toast.LENGTH_SHORT).show();
                        successful[0] = true;
                    }
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await(TIMEOUT, TIMEOUT_TIME_UNIT);

            if (successful[0] == false) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new BasicRemoteException(UNKNOWN_ERROR);
        }
    }

    private void addTagsToBody(MultipartBody.Builder bodyBuilder, ArrayList<String> tags) {
        if (tags.size() == 0) {
            bodyBuilder.addFormDataPart("tags", "");

            return;
        }

        for (int i = 0; i < tags.size(); i++) {
            bodyBuilder.addFormDataPart("tags[" + i + "]", tags.get(i));
        }
    }

    @Override
    public boolean updatePostLike(PostModel post, String userId, boolean liked) throws RemoteDataException {

        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        final HashMap<String, Object> params = new HashMap<>();
        params.put("postID", post.getId());

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.POST,
                        API_POST_LIKE_URL,
                        new JSONObject(params),
                        future, future) {
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
    public ArrayList<PostModel> fetchPOIPosts(PoiModel poi, int contentOffset, int contentLimit) throws RemoteDataException {
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