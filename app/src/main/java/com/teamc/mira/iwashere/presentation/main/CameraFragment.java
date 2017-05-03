package com.teamc.mira.iwashere.presentation.main;

import android.content.Context;
import android.content.Intent;

import android.graphics.Camera;
import android.os.Bundle;
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
import com.teamc.mira.iwashere.data.source.remote.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.UserRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.AuthInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiMapInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiMapInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.SignoutInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.Repository;
import com.teamc.mira.iwashere.domain.repository.UserRepository;
import com.teamc.mira.iwashere.presentation.auth.AuthenticateActivity;
import com.teamc.mira.iwashere.presentation.camera.CameraInit;


public class CameraFragment extends Fragment implements View.OnClickListener {

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

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        return v;
    }



    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CameraInit.class);
        int i = v.getId();
        Bundle b = new Bundle();

        if (i == R.id.photoBtn) {
            b.putString("key", "photo");
            intent.putExtras(b);
            startActivity(intent);
        }
        if(i==R.id.videoBtn){
            b.putString("key","video");
            intent.putExtras(b);
            startActivity(intent);
        }
        if(i==R.id.galleryBtn){
            b.putString("key","gallery");
            intent.putExtras(b);
            startActivity(intent);
        }
    }
}


