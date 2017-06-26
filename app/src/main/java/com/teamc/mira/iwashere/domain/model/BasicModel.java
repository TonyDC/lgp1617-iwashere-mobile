package com.teamc.mira.iwashere.domain.model;

import com.teamc.mira.iwashere.presentation.misc.PoiMapMarker;

import java.io.Serializable;

public class BasicModel implements Serializable{
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

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj))
            return true;

        return ((PoiModel) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}

