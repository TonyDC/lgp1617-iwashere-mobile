package com.teamc.mira.iwashere.presentation.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.RouteRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.RouteDetailInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.presentation.misc.MapFragment;
import com.teamc.mira.iwashere.presentation.misc.costum_components.ViewMore;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RouteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTE = "route";
    private static final String TAG = RouteDetailActivity.class.getSimpleName();

    protected RouteModel route;

    private ViewMore mDescription;
    private SwipeRefreshLayout mSwipeContainer;
    private NestedScrollView mNestedScrollView;
    private MapFragment mMapFragment;

    private ArrayList<String> mPoiNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        route = (RouteModel) getIntent().getSerializableExtra(EXTRA_ROUTE);

        setToolBar();
        getSupportActionBar().setTitle(route.getName());

        mDescription = (ViewMore) findViewById(R.id.routeDescritpion);
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRouteDetails(route);
            }
        });

        mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedView);


        setRouteInfo();

        fetchRouteDetails(route);
    }

    private void refreshPreview() {
        RoutePreviewFragment f = new RoutePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ROUTE, route);
        f.setArguments(args);
        Log.d(TAG, f.toString());
        mMapFragment = f;
        mMapFragment.setListener(new MapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mSwipeContainer.requestDisallowInterceptTouchEvent(true);
                mNestedScrollView.requestDisallowInterceptTouchEvent(true);
                Log.d(TAG, "Disallow request");
            }
        });


        getSupportFragmentManager().beginTransaction().add(R.id.map, f, "map").commit();
    }

    public void fetchRouteDetails(RouteModel route) {
        mSwipeContainer.setRefreshing(true);

        TemplateInteractor.CallBack callBack = new TemplateInteractor.CallBack<RouteModel>() {
            @Override
            public void onSuccess(RouteModel result) {
                RouteDetailActivity.this.route = result;
                setRouteInfo();
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onNetworkError() {
                Toast.makeText(RouteDetailActivity.this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onError(String code, String message) {
                Toast.makeText(RouteDetailActivity.this, getString(R.string.error_request), Toast.LENGTH_SHORT).show();
                mSwipeContainer.setRefreshing(false);
            }
        };

        new RouteDetailInteractorImpl(ThreadExecutor.getInstance() , MainThreadImpl.getInstance(),
                callBack,
                new RouteRepositoryImpl(this),
                route).execute();
    }

    private void setPoiList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listOfPois);
        recyclerView.setNestedScrollingEnabled(true);

        ArrayList<PoiModel> routePois =  route.getPois();
        mPoiNames = new ArrayList<>();

        for (int i = 0; i < routePois.size(); i++){
            Log.d(TAG, "Adding to route's pois list: poiId-"+routePois.get(i).getId());
            mPoiNames.add((i+1) + " - " + routePois.get(i).getName());
        }

        final CustomArrayAdapter adapter = new CustomArrayAdapter(this,mPoiNames);
        adapter.setOnItemClickListener(new CustomArrayAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                PoiModel poi = route.getPois().get(position);

                Intent intent = new Intent(RouteDetailActivity.this, PoiDetailActivity.class);
                intent.putExtra(PoiDetailActivity.POI, poi);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void setRouteInfo(){
        mDescription.setText(route.getDescription());
        refreshPreview();
        setPoiList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}
