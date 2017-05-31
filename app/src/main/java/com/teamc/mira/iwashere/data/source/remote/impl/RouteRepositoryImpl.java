package com.teamc.mira.iwashere.data.source.remote.impl;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.base.AbstractRepository;
import com.teamc.mira.iwashere.data.source.remote.base.ServerUrl;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.domain.repository.remote.RouteRepository;
import com.teamc.mira.iwashere.util.JsonObjectRequestWithNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.API;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.ROUTE;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT;
import static com.teamc.mira.iwashere.data.source.remote.base.ServerUrl.TIMEOUT_TIME_UNIT;

public class RouteRepositoryImpl extends AbstractRepository implements RouteRepository{
    private static final String TAG = RouteRepositoryImpl.class.getSimpleName();
    private static final String ROUTE_URL = ServerUrl.getUrl() + API + ROUTE;

    public RouteRepositoryImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public RouteRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public RouteModel fetchRoute(RouteModel route) throws RemoteDataException {
        return fetchRoute(route.getId());
    }

    @Override
    public RouteModel fetchRoute(String routeId) throws RemoteDataException {
        RequestQueue queue = mRequestQueue;

        String url = ROUTE_URL + "/" + routeId;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        Log.d(TAG, url);

        JsonObjectRequestWithNull request = new JsonObjectRequestWithNull(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(TIMEOUT, TIMEOUT_TIME_UNIT); // this will block

            return new RouteModel(response);
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            e.printStackTrace();
            handleError(e);

            return null;
        }
    }

    @Override
    public float fetchRating(String routeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float fetchUsersRating(String routeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float fetchRoutesPois(String routeId) {
        throw new UnsupportedOperationException();
    }
}
