package com.teamc.mira.iwashere.domain.model;

import com.teamc.mira.iwashere.domain.model.util.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ContentModel {
    private String id;
    private String createdAt;
    private String type;
    private String description;
    private boolean likedByUser;
    private ArrayList<String> tags;
    private Resource resource;

    public ContentModel(String id, String url) {
        this.id = id;
        this.resource = new Resource(url);
    }

    public ContentModel(JSONObject content) throws JSONException {
        this.id = content.getString("postId");
        this.createdAt = content.getString("postDate");
        this.description = content.getString("description");
        this.likedByUser = content.getBoolean("likedByUser");
        this.tags = new ArrayList<>();

        JSONArray tagList = content.getJSONArray("tags");
        for (int i = 0; i < tagList.length(); i++) {
            JSONObject tag = null;
            try {
                tag = tagList.getJSONObject(i);
                this.tags.add(tag.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // optional fields
        try {
            this.type = content.getString("type");
            this.resource = new Resource(content.getString("url"));
        } catch (JSONException e) {
            e.getCause();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
