package com.teamc.mira.iwashere.domain.model;

import com.teamc.mira.iwashere.domain.model.util.Resource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class PoiModel extends BasicModel implements Serializable {
    private String description;
    private String address;
    private String longitude;
    private String latitude;
    private float rating;
    private int ratingCount;
    private float userRating;

    private ArrayList<Resource> photos = new ArrayList<>();
    private ArrayList<ContentModel> content = new ArrayList<>();

    private ArrayList<PoiModel> relatedPois = new ArrayList<>();

    private boolean reminder;

    public PoiModel() {
        super();
    }

    public PoiModel(String id,
                    String name,
                    String description,
                    String address,
                    String longitude,
                    String latitude,
                    ArrayList<Resource> photos,
                    ArrayList<ContentModel> content,
                    ArrayList<PoiModel> relatedContent) {
        super(id, name);
        this.description = description;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.photos = photos;
        this.content = content;
        this.relatedPois = relatedContent;
    }

    public PoiModel(JSONObject poi) throws JSONException {
        super(poi.getString("poiId"), poi.getString("name"));

        if(poi.has("description")) this.description = poi.getString("description");
        if(poi.has("address")) this.address = poi.getString("address");
        if(poi.has("longitude")) this.longitude = poi.getString("longitude");
        if(poi.has("latitude")) this.latitude = poi.getString("latitude");
    }

    public PoiModel(String id, String name, String description, String address, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public ArrayList<Resource> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Resource> photos) {
        this.photos = photos;
    }

    public ArrayList<ContentModel> getContent() {
        return content;
    }

    public void setContent(ArrayList<ContentModel> content) {
        this.content = content;
    }

    public ArrayList<PoiModel> getRelatedPois() {
        return relatedPois;
    }

    public void setRelatedPois(ArrayList<PoiModel> relatedPois) {
        this.relatedPois = relatedPois;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}