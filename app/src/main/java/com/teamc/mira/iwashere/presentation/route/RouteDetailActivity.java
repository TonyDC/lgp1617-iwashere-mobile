package com.teamc.mira.iwashere.presentation.route;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.presentation.misc.ViewMore;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class RouteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POIS = "pois";
    public static final String EXTRA_ROUTE = "route";

    protected ArrayList<PoiModel> routePois = new ArrayList<>();
    protected RouteModel route;

    private ViewMore mDescription;
    private String[] mPoiNames;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Set description
        View viewDescriptionViewMore = findViewById(R.id.routeDescription);
        mDescription = new ViewMore(viewDescriptionViewMore, this, route.getDescription());
        mDescription.apply();

        ListView listView = (ListView) findViewById(R.id.listOfPois);
        mPoiNames = new String[routePois.size()];
        for (int i = 0; i < routePois.size(); i++){
            mPoiNames[i]=routePois.get(i).getName();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, mPoiNames);
        listView.setAdapter(adapter);
    }


}
