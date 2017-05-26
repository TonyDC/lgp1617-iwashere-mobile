package com.teamc.mira.iwashere.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.impl.SearchRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiMapInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.SearchInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.domain.model.SearchModel;
import com.teamc.mira.iwashere.domain.model.TagModel;
import com.teamc.mira.iwashere.presentation.misc.PoiMapMarker;
import com.teamc.mira.iwashere.presentation.searchList.ChildRow;
import com.teamc.mira.iwashere.presentation.searchList.SearchExpandableListAdapter;
import com.teamc.mira.iwashere.presentation.searchList.ParentRow;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.util.ArrayList;
import java.util.HashMap;

import static com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity.POI;

public class MainMapFragment extends Fragment implements OnSearchViewListener {

    private static final String TAG = MainMapFragment.class.getSimpleName();

    private BaseMaterialSearchView mSearchView;
    private SearchExpandableListAdapter mSearchListAdapter;
    private ExpandableListView mSearchList;
    private ArrayList<ParentRow> mCategoriesList = new ArrayList<>();
    private View mRootView;
    private Context mContext;

    private PoiMapFragment poiMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main_map, container, false);

        Toolbar myToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        mSearchView = (BaseMaterialSearchView) mRootView.findViewById(R.id.sv);
        mSearchView.setOnSearchViewListener(this);

        poiMapFragment = (PoiMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        setHasOptionsMenu(true);
        getActivity().setTitle(null);
        mCategoriesList = new ArrayList<>();
        displaySearchResults(mRootView);
        expandCategories();

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

    }

    private void searchForResults(String query) {

        TemplateInteractor.CallBack callBack = new TemplateInteractor.CallBack<SearchModel>() {
            @Override
            public void onSuccess(SearchModel searchModel) {
                Log.d(TAG, "PoiMapInteractor.CallBack SEARCH onSuccess");
                onSearchPoiFetch(searchModel);
            }

            @Override
            public void onNetworkError() {
                Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String code, String message) {
                if (message == null || message.length() == 0)
                    message = getString(R.string.error_fetch);

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        };

        LatLng latLng = poiMapFragment.getPosition();

        SearchInteractorImpl searchInteractor = new SearchInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                callBack,
                new SearchRepositoryImpl(getContext()),
                query, latLng.latitude, latLng.longitude
        );
        searchInteractor.execute();

        Log.d(TAG, "Searched Query: " + query + " Lat: " + latLng.latitude + " Lng: " + latLng.longitude);
    }

    private void onSearchPoiFetch(SearchModel searchModel) {

        mCategoriesList = new ArrayList<>();

        if (!searchModel.getPois().isEmpty()) {
            ArrayList<ChildRow> childRowsPois = new ArrayList<>();
            PoiModel model;
            ParentRow parentRow;
            for (int i = 0; i < searchModel.getPois().size(); i++) {
                model = searchModel.getPois().get(i);
                Log.d(TAG, "POI MODEL SEARCH: " + model.getName());
                childRowsPois.add(new ChildRow(R.drawable.map_marker, model));
            }
            parentRow = new ParentRow("Points of Interest", childRowsPois);
            mCategoriesList.add(parentRow);
        }

        if (!searchModel.getRoutes().isEmpty()) {
            ArrayList<ChildRow> childRowsRoutes = new ArrayList<>();
            RouteModel model;
            ParentRow parentRow;
            for (int i = 0; i < searchModel.getRoutes().size(); i++) {
                model = searchModel.getRoutes().get(i);
                Log.d(TAG, "ROUTE MODEL SEARCH: " + model.getName());
                childRowsRoutes.add(new ChildRow(R.drawable.walk, model));
            }
            parentRow = new ParentRow("Routes", childRowsRoutes);
            mCategoriesList.add(parentRow);
        }

        if (!searchModel.getTags().isEmpty()) {
            ArrayList<ChildRow> childRowsTags = new ArrayList<>();
            TagModel model;
            ParentRow parentRow;
            for (int i = 0; i < searchModel.getTags().size(); i++) {
                model = searchModel.getTags().get(i);
                Log.d(TAG, "TAG MODEL SEARCH: " + model.getName());
                childRowsTags.add(new ChildRow(R.drawable.pound, model));
            }
            parentRow = new ParentRow("Tags", childRowsTags);
            mCategoriesList.add(parentRow);
        }

        displaySearchResults(mRootView);
        expandCategories();
    }

    private void expandCategories() {
        int count = mSearchListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mSearchList.expandGroup(i);
        }
    }

    private void displaySearchResults(View rooView) {
        mSearchList = (ExpandableListView) rooView.findViewById(R.id.expandableListView_search);
        mSearchListAdapter = new SearchExpandableListAdapter(getActivity(), mCategoriesList);
        mSearchList.setAdapter(mSearchListAdapter);
    }

    @Override
    public void onSearchViewShown() {
        Log.d(TAG, "onSearchViewShown: clicked");
    }

    @Override
    public void onSearchViewClosed() {
        Log.d(TAG, "onSearchViewClosed: clicked");
        mCategoriesList = new ArrayList<>();
        displaySearchResults(mRootView);
        expandCategories();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);
        if (query.length() != 0) {
            searchForResults(query);
        } else {
            mCategoriesList = new ArrayList<>();
            displaySearchResults(mRootView);
        }
        expandCategories();
        return true;
    }

    @Override
    public void onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: " + newText);
        if (newText.length() != 0) {
            searchForResults(newText);
        } else {
            mCategoriesList = new ArrayList<>();
            displaySearchResults(mRootView);
        }
        expandCategories();
    }
}