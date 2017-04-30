package com.teamc.mira.iwashere.domain.model;

/**
 * Created by Duart on 12/04/2017.
 */

public class BasicModel {
    protected String id;
    protected String name;

    public BasicModel() {
    }

    public BasicModel(String id, String name) {
        this.id = id;
        this.name = name;
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

