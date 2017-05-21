package com.teamc.mira.iwashere.domain.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by LukášKonkoľ on 30.04.2017.
 */

public class RouteModel extends BasicModel implements Serializable {
    private String description;
    private String text;
    private ArrayList<PoiModel> pois = new ArrayList<>();

    public RouteModel() {
        super();
    }

    public RouteModel(String id, String name, String description, String text) {
        super(id, name);
        this.description = description;
        this.text = text;
    }

    public RouteModel(JSONObject route) throws JSONException {
        super(route.getString("routeId"), route.getString("name"));

        if (route.has("description")) this.description = route.getString("description");
        if (route.has("text")) this.text = route.getString("text");
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

    public String getText() {
        return text;
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

    public void setText(String text) {
        this.text = text;
    }
}
