package com.teamc.mira.iwashere.data.source.remote;

import com.android.volley.RequestQueue;

public class TestSingleton implements MySingleton {

    private final RequestQueue mRequestQueue;

    @Override
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public TestSingleton(RequestQueue requestQueue){
        this.mRequestQueue = requestQueue;
    }
}
