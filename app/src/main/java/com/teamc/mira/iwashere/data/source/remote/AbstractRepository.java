package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.teamc.mira.iwashere.domain.repository.Repository;

/**
 * Created by Duart on 16/04/2017.
 */

public class AbstractRepository {

    RequestQueue mRequestQueue;

    public AbstractRepository(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    public AbstractRepository(Context context){
        this.mRequestQueue = Volley.newRequestQueue(context);
    }
}
