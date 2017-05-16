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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;

import com.teamc.mira.iwashere.data.source.remote.impl.UserRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.AuthInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.SignoutInteractorImpl;
import com.teamc.mira.iwashere.domain.repository.remote.UserRepository;

import com.teamc.mira.iwashere.presentation.auth.AuthenticateActivity;
import com.teamc.mira.iwashere.presentation.misc.AboutActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;


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
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

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

    }

}


