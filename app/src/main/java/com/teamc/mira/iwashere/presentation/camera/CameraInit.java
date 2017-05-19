package com.teamc.mira.iwashere.presentation.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.PostRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PostInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PostInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;
import com.teamc.mira.iwashere.presentation.main.MainActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

public class CameraInit extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 1;

    private PostModel post;
    FirebaseAuth auth;
    private ImageView imageView;
    private VideoView videoView;
    private Button postButton;
    private Uri resourceToUploadUri;
    private EditText description_text, poi_text;
    String key = "";
    ArrayList<String> tags = null;
    String poiId = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Bundle b = getIntent().getExtras();
        if(b != null)
            key = b.getString("key");

        auth = FirebaseAuth.getInstance();

        imageView = (ImageView) this.findViewById(R.id.picturedisplay);
        videoView = (VideoView) this.findViewById(R.id.videodisplay);
        postButton = (Button) this.findViewById(R.id.postBtn);
        description_text = (EditText) this.findViewById(R.id.description_text);
        poi_text = (EditText) this.findViewById(R.id.poi_text);

        callCamera();

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
            resourceToUploadUri = Uri.fromFile(f);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        else if(key.equals("video")) {
            cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            File f = new File(Environment.getExternalStorageDirectory(), imageFileName + ".mp4");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            resourceToUploadUri = Uri.fromFile(f);
            startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);
        }
        else if(key.equals("gallery")) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        else{
            Toast.makeText(CameraInit.this, key, Toast.LENGTH_SHORT).show();
            return;
        }



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK){Intent intent = new Intent(CameraInit.this, MainActivity.class);
            startActivity(intent);
            finish();}

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = getBitmap(resourceToUploadUri);
            videoView.setVisibility(View.GONE);
            imageView.setImageBitmap(photo);
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            imageView.setVisibility(View.GONE);
            videoView.setVideoURI(resourceToUploadUri);
            videoView.setZOrderOnTop(true);
            videoView.start();
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Bitmap photo = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(scaleBitmap(photo));
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

    private Bitmap scaleBitmap(Bitmap myBitmap){
        int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
        return scaled;
    }

    public void onClick(View v) {

        int i = v.getId();

        if(i == R.id.postBtn){
            Toast.makeText(CameraInit.this, key, Toast.LENGTH_SHORT).show();

            MainThread mainThread = MainThreadImpl.getInstance();
            Executor executor = ThreadExecutor.getInstance();
            PostRepository postRepository = new PostRepositoryImpl(getApplicationContext());
            PostInteractor.CallBack callback = new PostInteractor.CallBack() {

                @Override
                public void onNetworkFail() {
                    Toast.makeText(getApplicationContext(), R.string.error_connection, LENGTH_SHORT).show();
                }

                @Override
                public void onError(String code, String message) {
                    Toast.makeText(getApplicationContext(), R.string.error_request, LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(PostModel newPost) {
                }
            };

            PostInteractor postInteractor = new PostInteractorImpl(
                    executor,
                    mainThread,
                    callback,
                    postRepository,
                    post,
                    auth.getCurrentUser().getUid(),
                    poiId,
                    description_text.getText().toString(),
                    new Resource(resourceToUploadUri.toString()),
                    tags
                    );

            postInteractor.execute();

        }

    }

}