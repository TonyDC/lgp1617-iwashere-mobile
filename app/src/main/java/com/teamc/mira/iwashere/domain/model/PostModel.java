package com.teamc.mira.iwashere.domain.model;

import com.teamc.mira.iwashere.domain.model.util.Resource;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Estrada on 17/05/2017.
 */

public class PostModel extends BasicModel implements Serializable {

    String description;
    Resource resource;
    String poiId;
    String userId;
    ArrayList<String> tags;

    public PostModel(){
        super();
    }

    public PostModel(
            String id,
            String name,
            String userId,
            String description,
            String poiId,
            Resource resource,
            ArrayList<String> tags
    ){
        super(id, name);
        this.userId = userId;
        this.description = description;
        this.resource = resource;
        this.poiId = poiId;
        this.tags = tags;

    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getPoiId(){
        return this.poiId;
    }

    public void setPoiId(String poiId){
        this.poiId = poiId;
    }


    public Resource getResource(){
        return this.resource;
    }

    public void setResource(Resource resource){
        this.resource = resource;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public ArrayList<String> getTags(){
        return this.tags;
    }

    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }
}
