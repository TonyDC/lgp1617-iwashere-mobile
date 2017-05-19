package com.teamc.mira.iwashere.presentation.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;

import java.util.ArrayList;

public class RouteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POIS = "pois";
    public static final String EXTRA_ROUTE = "route";

    protected ArrayList<PoiModel> routePois = new ArrayList<>();
    protected RouteModel route;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
