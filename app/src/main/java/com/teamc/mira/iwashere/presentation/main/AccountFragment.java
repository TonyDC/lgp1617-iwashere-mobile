package com.teamc.mira.iwashere.presentation.main;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Button;
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
import com.teamc.mira.iwashere.presentation.camera.CameraApi;
import com.teamc.mira.iwashere.presentation.misc.AboutActivity;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.util.ArrayList;



class AccountFragment extends Fragment implements View.OnClickListener{

    View v;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_account, container, false);
        v.findViewById(R.id.poi_test).setOnClickListener(this);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        /*PoiMapInteractor.CallBack callBack = new PoiMapInteractor.CallBack() {
            @Override
            public void onSuccess(ArrayList<PoiModel> poiModels) {
                TextView textView = (TextView) v.findViewById(R.id.hello_world_account);
                textView.setText(" POI1: "+ poiModels.get(0).getName()+"\n POI2: "+poiModels.get(1).getName());
                Toast.makeText(getActivity(), ""+poiModels.size(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), "Falhou", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNetworkError() {
                Toast.makeText(getActivity(), "Falha na conecção", Toast.LENGTH_LONG).show();
            }
        };
        PoiRepository poiRepository = new PoiRepositoryImpl(getActivity());
        PoiMapInteractorImpl poiMapInteractor = new PoiMapInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                callBack,
                poiRepository,
                43,44,43,44
        );

        poiMapInteractor.execute();*/

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_about:
                startActivity(new Intent(this.getContext(), AboutActivity.class));
                break;
            case R.id.action_logout:
                final Context context = getContext();
                AuthInteractor.Callback callback = new AuthInteractor.Callback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(context, AuthenticateActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onFail(String code, String message) {
                        Toast.makeText(getContext(),message, Toast.LENGTH_SHORT).show();
                    }
                };
                UserRepository repository = new UserRepositoryImpl(getContext());
                new SignoutInteractorImpl(
                        ThreadExecutor.getInstance(),
                        MainThreadImpl.getInstance(),
                        callback,
                        repository
                    ).execute();


                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.poi_test) {
            startActivity(new Intent(this.getContext(), CameraApi.class));
        }
    }
}


