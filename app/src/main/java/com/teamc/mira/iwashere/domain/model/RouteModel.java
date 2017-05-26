package com.teamc.mira.iwashere.domain.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class RouteModel extends BasicModel {
    private String description = "";
    private ArrayList<PoiModel> pois = new ArrayList<>();

    public RouteModel() {
        super();
    }

    public RouteModel(String id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public RouteModel(JSONObject route) throws JSONException {
        super(route.getString("routeId"), route.getString("name"));

        if (route.has("description")) this.description = route.getString("description");
        if (route.has("pois")) {
            JSONArray poisArray = route.getJSONArray("pois");
            JSONObject poi;
            for(int i = 0; i < poisArray.length(); i++){
                try{
                    poi = poisArray.getJSONObject(i);
                    this.pois.add(new PoiModel(poi));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<PoiModel> getPois() {
        return pois;
    }

    public void setPois(ArrayList<PoiModel> pois) {
        this.pois = pois;
    }
}
