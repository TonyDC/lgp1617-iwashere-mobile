package com.teamc.mira.iwashere.presentation.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.teamc.mira.iwashere.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraInit extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    private ImageView imageView;
    private VideoView videoView;
    private Button postButton;
    private Uri imageToUploadUri;
    private EditText description_text, poi_text;
    String key = "";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Bundle b = getIntent().getExtras();
        if(b != null)
            key = b.getString("key");


        imageView = (ImageView) this.findViewById(R.id.picturedisplay);
        videoView = (VideoView) this.findViewById(R.id.videodisplay);
        postButton = (Button) this.findViewById(R.id.postBtn);
        description_text = (EditText) this.findViewById(R.id.description_text);
        poi_text = (EditText) this.findViewById(R.id.poi_text);

        callCamera();

        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(CameraInit.this, key, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callCamera(){
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode);
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IWasHere" + timeStamp + "_";
        Intent cameraIntent;


        if(key.equals("photo")) {
            cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(Environment.getExternalStorageDirectory(), imageFileName + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            imageToUploadUri = Uri.fromFile(f);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        else if(key.equals("video")) {
            cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            File f = new File(Environment.getExternalStorageDirectory(), imageFileName + ".mp4");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            imageToUploadUri = Uri.fromFile(f);
            startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);
        }
        else{
            Toast.makeText(CameraInit.this, key, Toast.LENGTH_SHORT).show();
            return;
        }



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = getBitmap(imageToUploadUri);
            videoView.setVisibility(View.GONE);
            imageView.setImageBitmap(photo);
            //imageView.setImageURI(imageToUploadUri);
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            imageView.setVisibility(View.GONE);
            videoView.setVideoURI(imageToUploadUri);
            videoView.setZOrderOnTop(true);
            videoView.start();
        }

    }

    private Bitmap getBitmap(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Rect rect = new Rect(0, 0, 0, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = getInSampleSize(options, 64, 64);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, rect, options);
        Bitmap scaledBitmap = scaleBitmap(bitmap);
        return scaledBitmap;
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
/*
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
*/
    private Bitmap scaleBitmap(Bitmap myBitmap){
        int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
        return scaled;
    }

}