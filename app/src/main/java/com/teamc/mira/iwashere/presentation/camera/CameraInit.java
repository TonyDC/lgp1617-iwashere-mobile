package com.teamc.mira.iwashere.presentation.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class CameraInit extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;

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

        setToolBar();

        Bundle b = getIntent().getExtras();
        if (b != null)
            key = b.getString("key");

        auth = FirebaseAuth.getInstance();

        imageView = (ImageView) this.findViewById(R.id.picturedisplay);
        videoView = (VideoView) this.findViewById(R.id.videodisplay);
        postButton = (Button) this.findViewById(R.id.postBtn);
        description_text = (EditText) this.findViewById(R.id.description_text);
        poi_text = (EditText) this.findViewById(R.id.poi_text);

        callCamera();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });

    }

    public void callCamera() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IWasHere" + timeStamp + "_";
        Intent cameraIntent;


        if (key.equals("photo")) {
            cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(Environment.getExternalStorageDirectory(), imageFileName + ".jpg");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(CameraInit.this, CameraInit.this.getApplicationContext().getPackageName() + ".provider", f));
                resourceToUploadUri = FileProvider.getUriForFile(CameraInit.this, CameraInit.this.getApplicationContext().getPackageName() + ".provider", f);
            } else {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                resourceToUploadUri = Uri.fromFile(f);
            }
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        } else if (key.equals("video")) {
            cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            File f = new File(Environment.getExternalStorageDirectory(), imageFileName + ".mp4");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(CameraInit.this, CameraInit.this.getApplicationContext().getPackageName() + ".provider", f));
                resourceToUploadUri = FileProvider.getUriForFile(CameraInit.this, CameraInit.this.getApplicationContext().getPackageName() + ".provider", f);
            } else {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                resourceToUploadUri = Uri.fromFile(f);
            }
            startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);


        } else if (key.equals("gallery")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);


        } else {
            Toast.makeText(CameraInit.this, key, LENGTH_SHORT).show();
            return;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Intent intent = new Intent(CameraInit.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            String filePath = resourceToUploadUri.getPath();
            Bitmap photo = getBitmap(resourceToUploadUri);
            photo = requireRotation(filePath, photo);

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
            resourceToUploadUri = data.getData();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(resourceToUploadUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap photo = BitmapFactory.decodeFile(picturePath);
                photo = requireRotation(picturePath, photo);
                imageView.setImageBitmap(photo);

            } else {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resourceToUploadUri);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

    }

    public Bitmap requireRotation(String filePath, Bitmap photo) {
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

    private Bitmap scaleBitmap(Bitmap myBitmap) {
        int nh = (int) (myBitmap.getHeight() * (512.0 / myBitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
        return scaled;
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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

    public String getFileExtension(String path){
        String filename = path;
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        return extension;
    }

    public void sendPost(){ Toast.makeText(CameraInit.this, key, Toast.LENGTH_SHORT).show();

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
                System.out.println("POST DONE");
                Toast.makeText(getApplicationContext(), "POST DONE", LENGTH_SHORT).show();
            }
        };

        PostInteractor postInteractor = new PostInteractorImpl(
                executor,
                mainThread,
                callback,
                postRepository,
                post,
                poiId,
                description_text.getText().toString(),
                tags,
                new File(getRealPathFromURI(resourceToUploadUri))
        );

        postInteractor.execute();}

}