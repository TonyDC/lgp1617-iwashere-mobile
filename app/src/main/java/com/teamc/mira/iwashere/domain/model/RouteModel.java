package com.teamc.mira.iwashere.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LukášKonkoľ on 30.04.2017.
 */

public class RouteModel extends BasicModel implements Serializable {
    private String description;
    private String text;

    public RouteModel() {
        super();
    }

    public RouteModel(String id, String name, String description, String text) {
        super(id, name);
        this.description = description;
        this.text = text;
    }

    public RouteModel(JSONObject route) throws JSONException {
        this.id = route.getString("routeId");
        this.name = route.getString("name");
        this.description = route.getString("description");
        this.text = route.getString("text");
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
