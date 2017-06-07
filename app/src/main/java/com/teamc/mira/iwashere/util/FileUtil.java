package com.teamc.mira.iwashere.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class FileUtil {
    public static  Bitmap scaleBitmap(Bitmap myBitmap, int scale) {
        int nh = (int) (myBitmap.getHeight() * (((float)scale) / myBitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, scale, nh, true);
        return scaled;
    }

    public static String getFileExtension(String path){
        String filename = path;
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        return extension;
    }

    public static Bitmap requireRotation(String filePath, Bitmap photo) {
        try {
            ExifInterface ei = new ExifInterface(filePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    photo = rotateImage(photo, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    photo = rotateImage(photo, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    photo = rotateImage(photo, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
