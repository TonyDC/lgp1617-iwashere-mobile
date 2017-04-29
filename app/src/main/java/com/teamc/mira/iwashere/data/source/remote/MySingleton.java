package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;

import com.android.volley.RequestQueue;

interface MySingleton {
    RequestQueue getRequestQueue();
}
