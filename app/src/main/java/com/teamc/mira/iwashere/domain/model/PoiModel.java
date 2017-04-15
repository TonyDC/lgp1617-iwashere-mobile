package com.teamc.mira.iwashere.domain.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Duart on 12/04/2017.
 */

public class PoiModel {
    private String id;
    private String name;
    private String description;
    private String address;
    private String longitude;
    private String latitude;

    private ArrayList<Bitmap> photos;
    private ArrayList<ContentModel> content;

    private ArrayList<PoiModel> relatedPois;

    private boolean reminder;

    public PoiModel(String id,
                    String name,
                    String description,
                    String address,
                    String longitude,
                    String latitude,
                    ArrayList<Bitmap> photos,
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

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Bitmap> photos) {
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
