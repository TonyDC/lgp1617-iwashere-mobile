package com.teamc.mira.iwashere.presentation.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.teamc.mira.iwashere.data.source.local.FileRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.impl.PostRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PostInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PostInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.repository.local.FileRepository;
import com.teamc.mira.iwashere.domain.repository.remote.PostRepository;
import com.teamc.mira.iwashere.presentation.main.MainActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;
import static com.teamc.mira.iwashere.util.FileUtil.requireRotation;
import static com.teamc.mira.iwashere.util.FileUtil.scaleBitmap;

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
    private EditText descriptionText, poi_text;
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
        descriptionText = (EditText) this.findViewById(R.id.description_text);
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

            Bitmap photo = new FileRepositoryImpl(this).getBitmap(resourceToUploadUri);
            photo = scaleBitmap(photo, 512);

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

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        FileRepository fileRepository = new FileRepositoryImpl(this);
        String realPath = fileRepository.getRealPathFromURI(resourceToUploadUri);

        PostInteractor postInteractor = new PostInteractorImpl(
                executor,
                mainThread,
                callback,
                postRepository,
                post, poiId, descriptionText.getText().toString(), tags,
                new File(realPath)
        );

        postInteractor.execute();}

}