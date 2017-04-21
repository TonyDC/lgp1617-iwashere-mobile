package com.teamc.mira.iwashere.data.source.remote;

/**
 * Created by Duart on 21/04/2017.
 */

public class ServerUrl {

    static final String DOMAIN = "duarte-asus";
    static final String PORT = "8080";

    public static String getUrl(){
        return "http://"+DOMAIN+":"+PORT;
    }

    //PATHS
    static final String API = "/api";

    static final String POI = "/poi";
    static final String RANGE = "/range";
}
