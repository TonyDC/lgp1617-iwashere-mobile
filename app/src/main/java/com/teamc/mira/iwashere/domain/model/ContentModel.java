package com.teamc.mira.iwashere.domain.model;

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
    private URL url;

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
            this.url = new URL(content.getString("url"));
        } catch (MalformedURLException | JSONException e) { }
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

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
