package com.teamc.mira.iwashere.presentation.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.presentation.misc.costum_components.ViewMore;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;

import java.util.ArrayList;

public class RouteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTE = "route";
    private static final String TAG = RouteDetailActivity.class.getSimpleName();

    protected ArrayList<PoiModel> routePois = new ArrayList<>();
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

    }

    private void refreshPreview() {
        RoutePreviewFragment f = new RoutePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ROUTE, route);
        f.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.map, f, "map").commit();
    }

    private void fetchRouteDetails(RouteModel route) {

    }

    private void setPoiList() {
        ListView listView = (ListView) findViewById(R.id.listOfPois);
        mPoiNames = new String[routePois.size()];
        for (int i = 0; i < routePois.size(); i++){
            mPoiNames[i]=routePois.get(i).getName();
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
