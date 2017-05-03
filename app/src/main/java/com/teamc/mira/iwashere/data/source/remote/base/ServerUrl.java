package com.teamc.mira.iwashere.data.source.remote.base;

import java.util.concurrent.TimeUnit;

/**
 * Created by Duart on 21/04/2017.
 */

public class ServerUrl {

    public static final String DOMAIN = "172.30.5.114";
    public static final String PORT = "8080";

    public static final long TIMEOUT = 3;
    public static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;
    public static final String AUTH = "/auth";

    public static String getUrl(){
        return "http://"+DOMAIN+":"+PORT;
    }

    //PATHS
    public static final String API = "/api";

    public static final String POI = "/poi";
    public static final String RANGE = "/range";
    public static final String MEDIA = "/media";
    public static final String RATING = "/rating";
    public static final String CONTENT = "/post";
    public static final String POI_CONTENT = "/poi_posts";
}