package com.teamc.mira.iwashere.data.source.remote.base;

import java.util.concurrent.TimeUnit;

public class ServerUrl {

    public static final String DOMAIN = "192.168.1.129";
    public static final String PORT = "8080";

    public static final long TIMEOUT = 3;
    public static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;
    public static final String AUTH = "/auth";

    public static String getUrl(){
        return "http://"+DOMAIN+":"+PORT;
    }

    //PATHS
    public static final String API = "/api";

    public static final String USER = "/user";
    public static final String UNAUTH = "/unauth";
    public static final String REGISTER = "/register";
    public static final String REGISTER_BY_PROVIDER = "/register-by-provider";

    public static final String POI = "/poi";
    public static final String RANGE = "/range";
    public static final String MEDIA = "/media";
    public static final String RATING = "/rating";
    public static final String SEARCH = "/search";
    public static final String CONTENT = "/post";
    public static final String POI_CONTENT = "/poi_posts";
    public static final String ROUTE = "/route";
}
