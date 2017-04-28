package com.teamc.mira.iwashere.data.source.remote;

import java.util.concurrent.TimeUnit;

/**
 * Created by Duart on 21/04/2017.
 */

public class ServerUrl {

    static final String DOMAIN = "172.30.5.114";
    static final String PORT = "8080";

    static final int TIMEOUT = 3000;
    static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static String getUrl(){
        return "http://"+DOMAIN+":"+PORT;
    }

    //PATHS
    static final String API = "/api";

    static final String POI = "/poi";
    static final String RANGE = "/range";
    static final String MEDIA = "/media";
    static final String RATING = "/rating";
    static final String CONTENT = "/post";
    static final String POI_CONTENT = "/poi_posts";
}
