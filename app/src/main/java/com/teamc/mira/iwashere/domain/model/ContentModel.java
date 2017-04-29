package com.teamc.mira.iwashere.domain.model;

/**
 * Created by Duart on 12/04/2017.
 */

public class ContentModel {
    public enum Type{
        PHOTO,
        VIDEO,
        TEXT,
        SOUND,
    }

    String url;
    String id;
    Type type = Type.PHOTO;

    public ContentModel(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public ContentModel(String url, String id, Type type) {
        this.url = url;
        this.id = id;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
