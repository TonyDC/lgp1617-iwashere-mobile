package com.teamc.mira.iwashere.domain.model;

import java.util.ArrayList;

/**
 * Created by LukášKonkoľ on 30.04.2017.
 */

public class SearchModel {
    private ArrayList<PoiModel> pois;
    private ArrayList<RouteModel> routes;
    private ArrayList<TagModel> tags;

    public SearchModel(ArrayList<PoiModel> pois, ArrayList<RouteModel> routes, ArrayList<TagModel> tags) {
        this.pois = pois;
        this.routes = routes;
        this.tags = tags;
    }

    public SearchModel() {
        this.pois = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public ArrayList<PoiModel> getPois() {
        return pois;
    }

    public ArrayList<RouteModel> getRoutes() {
        return routes;
    }

    public ArrayList<TagModel> getTags() {
        return tags;
    }

    public void setPois(ArrayList<PoiModel> pois) {
        this.pois = pois;
    }

    public void setRoutes(ArrayList<RouteModel> routes) {
        this.routes = routes;
    }

    public void setTags(ArrayList<TagModel> tags) {
        this.tags = tags;
    }
}
