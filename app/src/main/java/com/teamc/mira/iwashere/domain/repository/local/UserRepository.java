package com.teamc.mira.iwashere.domain.repository.local;

import android.net.Uri;

public interface UserRepository {

    String getDisplayName();

    String getToken();

    String getUID();

    Uri getPhotoUrl();

    String getEmail();
}
