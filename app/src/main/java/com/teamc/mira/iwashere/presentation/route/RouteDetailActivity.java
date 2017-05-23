package com.teamc.mira.iwashere.presentation.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.teamc.mira.iwashere.domain.repository.remote.RouteRepository;
import com.teamc.mira.iwashere.presentation.misc.costum_components.ViewMore;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RouteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTE = "route";
    private static final String TAG = RouteDetailActivity.class.getSimpleName();

    protected RouteModel route;

    private ViewMore mDescription;
    private SwipeRefreshLayout mSwipeContainer;

    private String[] mPoiNames;

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

        setRouteInfo();

        fetchRouteDetails(route);
    }

    private void refreshPreview() {
        RoutePreviewFragment f = new RoutePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ROUTE, route);
        f.setArguments(args);
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
        ListView listView = (ListView) findViewById(R.id.listOfPois);

        ArrayList<PoiModel> routePois = route.getPois();
        mPoiNames = new String[routePois.size()];

        for (int i = 0; i < routePois.size(); i++){
            Log.d(TAG, "Adding to route's pois list: poiId-"+routePois.get(i).getId());
            mPoiNames[i]= (i+1) + " - " + routePois.get(i).getName();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mPoiNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiModel poi = route.getPois().get(position);

                Intent intent = new Intent(RouteDetailActivity.this, PoiDetailActivity.class);
                intent.putExtra(PoiDetailActivity.POI, poi);
                startActivity(intent);
            }
        });
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
