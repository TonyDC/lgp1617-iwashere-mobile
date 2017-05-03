package com.teamc.mira.iwashere.domain.repository.local;

import android.media.session.MediaSession;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;

public interface UserRepository {

    String getDisplayName();

    String getToken();

    String getUID();

    Uri getPhotoUrl();

    String getEmail();
}
