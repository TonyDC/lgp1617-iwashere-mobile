package com.teamc.mira.iwashere.data.source.remote;

import java.util.concurrent.TimeUnit;

/**
 * Created by Duart on 21/04/2017.
 */

public class ServerUrl {

    static final String DOMAIN = "192.168.1.78";
    static final String PORT = "8080";

    static final long TIMEOUT = 3;
    static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;

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
