package com.teamc.mira.iwashere.data.source.remote;

import java.util.concurrent.TimeUnit;

public class ServerUrl {

    static final String DOMAIN = "192.168.1.78";
    static final String PORT = "8080";

    static final int TIMEOUT = 3000;
    static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static String getUrl(){
        return "http://"+DOMAIN+":"+PORT;
    }

    //PATHS
    static final String API = "/api";

    static final String USER = "/user";
    static final String UNAUTH = "/unauth";
    static final String REGISTER = "register";
    static final String REGISTER_BY_PROVIDER = "/register-by-provider";

    static final String POI = "/poi";
    static final String RANGE = "/range";
    static final String MEDIA = "/media";
    static final String RATING = "/rating";
    static final String CONTENT = "/post";
    static final String POI_CONTENT = "/poi_posts";
}
