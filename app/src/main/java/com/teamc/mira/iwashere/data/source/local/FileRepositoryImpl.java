package com.teamc.mira.iwashere.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import com.teamc.mira.iwashere.domain.repository.local.FileRepository;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileRepositoryImpl implements FileRepository {
    private Context mContext;

    public FileRepositoryImpl(Context mContext){
        this.mContext = mContext;
    }
    @Override
    public String getRealPathFromURI(Uri contentURI) {
/*
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        return  contentURI.toString();
        }
*/
        String result;
        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public Bitmap getBitmap(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Rect rect = new Rect(0, 0, 0, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = getInSampleSize(options, 64, 64);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, rect, options);
    }

    private int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight &&
                    (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
