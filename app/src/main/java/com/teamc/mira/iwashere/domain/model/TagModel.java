package com.teamc.mira.iwashere.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LukášKonkoľ on 30.04.2017.
 */

public class TagModel extends BasicModel implements Serializable {

    public TagModel() {
        super();
    }

    public TagModel(String id, String name) {
        super(id, name);
    }

    public TagModel(JSONObject tag) throws JSONException {
        this.id = tag.getString("tagId");
        this.name = tag.getString("name");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}