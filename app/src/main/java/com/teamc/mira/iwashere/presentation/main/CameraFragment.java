package com.teamc.mira.iwashere.presentation.main;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;

import com.teamc.mira.iwashere.presentation.camera.CameraInit;

import static com.teamc.mira.iwashere.presentation.main.CameraFragment.Intention.GALLERY;
import static com.teamc.mira.iwashere.presentation.main.CameraFragment.Intention.PHOTO;
import static com.teamc.mira.iwashere.presentation.main.CameraFragment.Intention.VIDEO;


public class CameraFragment extends Fragment implements View.OnClickListener {
    public static final String ANDROID_PERMISSION_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private Intention intention = PHOTO;

    protected enum Intention {
        PHOTO, VIDEO, GALLERY
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_camera_choice, container, false);

        v.findViewById(R.id.photoBtn).setOnClickListener(this);
        v.findViewById(R.id.videoBtn).setOnClickListener(this);
        v.findViewById(R.id.galleryBtn).setOnClickListener(this);

        return v;
    }



    public void onClick(View v) {

       int i = v.getId();

        if (i == R.id.photoBtn) {
            intention = PHOTO;
            checkPermissions();

        }
        if(i==R.id.videoBtn){
            intention = VIDEO;
            checkPermissions();
        }
        if(i==R.id.galleryBtn){
            intention = GALLERY;
            checkPermissions();
        }
    }

    private void checkPermissions() {
        String[] perms = {ANDROID_PERMISSION_WRITE_EXTERNAL_STORAGE};

        int permsRequestCode = 200;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            requestPermissions(perms, permsRequestCode);
        } else {
            onRequestPermissionsResult(permsRequestCode, perms, new int[]{PackageManager.PERMISSION_GRANTED});
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Intent intent = new Intent(getActivity(), CameraInit.class);
        Bundle b = new Bundle();
        if (permissions[0].equals(ANDROID_PERMISSION_WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (intention) {
                case PHOTO:
                    b.putString("key", "photo");
                    intent.putExtras(b);
                    startActivity(intent);

                    break;
                case VIDEO:
                    b.putString("key", "video");
                    intent.putExtras(b);
                    startActivity(intent);

                    break;

                case GALLERY:
                    b.putString("key", "gallery");
                    intent.putExtras(b);
                    startActivity(intent);

                    break;

                default:
                    b.putString("key", "photo");
                    intent.putExtras(b);
                    startActivity(intent);

                    break;
            }
        }
    }
}


