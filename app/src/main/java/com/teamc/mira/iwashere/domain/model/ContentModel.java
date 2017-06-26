package com.teamc.mira.iwashere.domain.model;

import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.model.util.ResourceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentModel {
    private Resource resource;
    private String id;
    private String createdAt;
    private Resource.Type type;
    private String description;
    private boolean likedByUser;
    private ArrayList<String> tags;

    public ContentModel(String id) {
        this.id = id;
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

        this.resource = ResourceFactory.getResource(content);
        this.type = resource.getType();

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

    public Resource.Type getType() {
        return type;
    }

    public void setType(Resource.Type type) {
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
