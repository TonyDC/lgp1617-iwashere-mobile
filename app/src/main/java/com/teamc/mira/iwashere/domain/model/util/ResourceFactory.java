package com.teamc.mira.iwashere.domain.model.util;

import org.json.JSONException;
import org.json.JSONObject;

public class ResourceFactory {
    public static Resource getResource(JSONObject object) throws JSONException {
        String type = object.getString("type");

        if (type.contains("image")) {
            return new ImageResource(object);
        } else {
            // TODO: 27/05/2017 get other types of resource
            return null;
        }
    }
}
