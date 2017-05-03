package com.teamc.mira.iwashere.data.source.local;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Duart on 03/05/2017.
 */

public class UserRepository implements com.teamc.mira.iwashere.domain.repository.local.UserRepository {
    private static final UserRepository ourInstance = new UserRepository();

    public static UserRepository getInstance() {
        return ourInstance;
    }

    private UserRepository() {
    }

    @Override
    public String getDisplayName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    @Override
    public String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public String getUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public Uri getPhotoUrl() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    }

    @Override
    public String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }
}
