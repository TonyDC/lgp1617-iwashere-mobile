package com.teamc.mira.iwashere.domain.repository.local;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

public interface FileRepository {

    File createImageFile() throws IOException;
}
