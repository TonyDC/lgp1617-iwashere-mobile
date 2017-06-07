package com.teamc.mira.iwashere.domain.repository.local;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public interface FileRepository {
    String getRealPathFromURI(Uri contentURI);

    Bitmap getBitmap(Uri uri);
}
