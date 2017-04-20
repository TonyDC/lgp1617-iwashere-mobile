package com.teamc.mira.iwashere.domain.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class PoiModel {
    private String id;
    private String name;
    private String description;
    private String address;
    private String longitude;
    private String latitude;

    private ArrayList<URL> photos = new ArrayList<>();
    private ArrayList<ContentModel> content = new ArrayList<>();

    private ArrayList<PoiModel> relatedPois = new ArrayList<>();

    private boolean reminder;

    public PoiModel(String id,
                    String name,
                    String description,
                    String address,
                    String longitude,
                    String latitude,
                    ArrayList<URL> photos,
                    ArrayList<ContentModel> content,
                    ArrayList<PoiModel> relatedContent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.photos = photos;
        this.content = content;
        this.relatedPois = relatedContent;
    }

    public PoiModel(JSONObject poi) throws JSONException {
        this.id = poi.getString("id");
        this.name = poi.getString("name");
        this.description = poi.getString("descritpion");
        this.address = poi.getString("address");
        this.longitude = poi.getString("longitude");
        this.latitude = poi.getString("latitude");
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

    public ArrayList<URL> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<URL> photos) {
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
}
