package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
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

/**
 * Created by LukášKonkoľ on 30.04.2017.
 */

public class SearchRepositoryImpl extends AbstractRepository implements SearchRepository {

    public static final String TAG = SearchRepositoryImpl.class.getSimpleName();
    private static final String API_SEARCH_URL = ServerUrl.getUrl() + ServerUrl.API + ServerUrl.SEARCH;
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

        return getSearchModelsFromRequest(future);
    }

    @Nullable
    SearchModel getSearchModelsFromRequest(RequestFuture<JSONObject> future) throws RemoteDataException {
        try {
            JSONObject response = future.get(3000, TimeUnit.MILLISECONDS); // this will block
            System.out.println(TAG + ": " + String.valueOf(response));


            SearchModel searchModel = new SearchModel();

            searchModel.setPois(getArrayListOfPoiModels(response.getJSONArray(JSON_POI)));
            searchModel.setRoutes(getArrayListOfRouteModels(response.getJSONArray(JSON_ROUTE)));
            searchModel.setTags(getArrayListOfTagModels(response.getJSONArray(JSON_TAG)));

            return searchModel;

        } catch (InterruptedException | ExecutionException e) {
            //check to see if the throwable in an instance of the volley error
            if (e.getCause() instanceof VolleyError) {
                //grab the volley error from the throwable and cast it back
                VolleyError volleyError = (VolleyError) e.getCause();
                //now just grab the network response like normal
                NetworkResponse networkResponse = volleyError.networkResponse;
                try {
                    Log.d(TAG, "raw data: " + new String(networkResponse.data));
                    JSONObject data = new JSONObject(new String(networkResponse.data));
                    Log.d(TAG, data.toString());

                    String code = data.getString("code");

                    throw (RemoteDataException) new BasicRemoteException(code);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    throw (RemoteDataException) new BasicRemoteException("unknown-error");
                }
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw (RemoteDataException) new BasicRemoteException("unknown-error");
        }
        return null;
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