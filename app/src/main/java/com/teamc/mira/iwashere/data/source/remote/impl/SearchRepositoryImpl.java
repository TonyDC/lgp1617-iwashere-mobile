package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.BasicRemoteException;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.domain.model.SearchModel;
import com.teamc.mira.iwashere.domain.model.TagModel;
import com.teamc.mira.iwashere.domain.repository.SearchRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchRepositoryImpl extends AbstractRepository implements SearchRepository {

    public static final String TAG = SearchRepositoryImpl.class.getSimpleName();
    private static final String API_SEARCH_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.SEARCH + "?query=";
    private static final String JSON_POI = "poi";
    private static final String JSON_ROUTE = "route";
    private static final String JSON_TAG = "tag";

    public SearchRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public SearchRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public SearchModel search(String searchQuery, double lat, double lng) throws RemoteDataException {
        // Instantiate the RequestQueue.
        RequestQueue queue = mRequestQueue;

        String url = API_SEARCH_URL;
        if (lat != 0.0 && lng != 0.0) {
            url = url.concat(searchQuery);
        } else url = url.concat(searchQuery + "&" + lat + "&" + lng);

        Log.d(TAG, url);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);
        try{
            return getSearchModelsFromRequest(future);
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            handleError(e);
            return null;
        }
    }

    @Nullable
    SearchModel getSearchModelsFromRequest(RequestFuture<JSONObject> future) throws InterruptedException, ExecutionException, TimeoutException, JSONException {
        JSONObject response = future.get(ServerUrl.TIMEOUT, ServerUrl.TIMEOUT_TIME_UNIT); // this will block
        System.out.println(TAG + ": " + String.valueOf(response));


        SearchModel searchModel = new SearchModel();

        searchModel.setPois(getArrayListOfPoiModels(response.getJSONArray(JSON_POI)));
        searchModel.setRoutes(getArrayListOfRouteModels(response.getJSONArray(JSON_ROUTE)));
        searchModel.setTags(getArrayListOfTagModels(response.getJSONArray(JSON_TAG)));

        return searchModel;
    }

    private ArrayList<PoiModel> getArrayListOfPoiModels(JSONArray jsonArray) throws JSONException {
        ArrayList<PoiModel> models = new ArrayList<>();
        PoiModel model;
        JSONObject object;

        System.out.println(TAG + ": " + String.valueOf(jsonArray));

        for (int i = 0; i < jsonArray.length(); i++) {
            object = jsonArray.getJSONObject(i);
            model = new PoiModel(object);
            models.add(model);
        }

        return models;
    }

    private ArrayList<RouteModel> getArrayListOfRouteModels(JSONArray jsonArray) throws JSONException {
        ArrayList<RouteModel> models = new ArrayList<>();
        RouteModel model;
        JSONObject object;

        System.out.println(TAG + ": " + String.valueOf(jsonArray));

        for (int i = 0; i < jsonArray.length(); i++) {
            object = jsonArray.getJSONObject(i);
            model = new RouteModel(object);
            models.add(model);
        }

        return models;
    }

    private ArrayList<TagModel> getArrayListOfTagModels(JSONArray jsonArray) throws JSONException {
        ArrayList<TagModel> models = new ArrayList<>();
        TagModel model;
        JSONObject object;

        System.out.println(TAG + ": " + String.valueOf(jsonArray));

        for (int i = 0; i < jsonArray.length(); i++) {
            object = jsonArray.getJSONObject(i);
            model = new TagModel(object);
            models.add(model);
        }

        return models;
    }
}