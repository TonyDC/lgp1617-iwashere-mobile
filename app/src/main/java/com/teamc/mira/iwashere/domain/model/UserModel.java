package com.teamc.mira.iwashere.domain.model;

import android.graphics.Bitmap;

/**
 * Created by Duart on 02/04/2017.
 */

public class UserModel {

    private String username;
    private String email;
    private Bitmap photo; // TODO: 02/04/2017 Verify type of photo to use

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Bitmap getPhoto() {
        return photo;
    }
}
