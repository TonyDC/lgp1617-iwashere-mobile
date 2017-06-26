package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;

import com.android.volley.RequestQueue;

public interface MySingleton {
    RequestQueue getRequestQueue();
}
