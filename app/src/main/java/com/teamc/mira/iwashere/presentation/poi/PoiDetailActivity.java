package com.teamc.mira.iwashere.presentation.poi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.impl.PostRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PoiContentInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiContentInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiDetailInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiRatingInteractorImpl;
import com.teamc.mira.iwashere.domain.model.ContentModel;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.presentation.misc.costum_components.ViewMore;
import com.teamc.mira.iwashere.threading.MainThreadImpl;
import com.teamc.mira.iwashere.util.ExpandableHeightGridView;
import com.teamc.mira.iwashere.util.ViewMoreGridView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class PoiDetailActivity extends AppCompatActivity {

    public static final String TAG = PoiDetailActivity.class.getSimpleName();
    public static final int CONTENT_LIMIT = 8;
    public static final String POI = "poi";

    PoiModel poi;
    int contentOffeset = 0;
    FirebaseAuth auth;

    /* View components */
    SliderLayout sliderShow;
    TextView poiRatingText;
    RatingBar userRatingBar;
    boolean moreContent = true;


    ViewMoreGridView gridView;
    private ArrayList<String> contentIdList;
    private ArrayList<Resource> contentResourceList;
    private SwipeRefreshLayout mSwipeContainer;
    private ViewMore mDescriptionViewMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        auth = FirebaseAuth.getInstance();

        mDescriptionViewMore = (ViewMore) findViewById(R.id.poiDescription);

        setToolBar();
        poi = (PoiModel) getIntent().getSerializableExtra(POI);
        setPoiInfo();

        // GridView size changes with number of components
        ExpandableHeightGridView mAppsGrid = (ExpandableHeightGridView) findViewById(R.id.grid_view_image_text);
        mAppsGrid.setExpanded(true);

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPoiInfo(poi);
            }
        });

        fetchPoiInfo(poi);
        mSwipeContainer.setRefreshing(true);

        userRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, final float rating,
                                        boolean fromUser) {

                if (!fromUser) {
                    return;
                }

                MainThread mainThread = MainThreadImpl.getInstance();
                Executor executor = ThreadExecutor.getInstance();
                PoiRepository poiRepository = new PoiRepositoryImpl(getApplicationContext());
                TemplateInteractor.CallBack callback = new TemplateInteractor.CallBack<PoiModel>() {

                    @Override
                    public void onNetworkError() {
                        Toast.makeText(getApplicationContext(), R.string.error_connection, LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String code, String message) {
                        userRatingBar.setRating(poi.getUserRating());
                        Toast.makeText(getApplicationContext(), R.string.error_request, LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(PoiModel updatedPoi) {
                        poi.setUserRating(rating);
                        poi.setRating(updatedPoi.getRating());
//                        setPoiRating(poi);
                    }
                };

                PoiRatingInteractorImpl poiRatingInteractor = new PoiRatingInteractorImpl(
                        executor,
                        mainThread,
                        callback,
                        poiRepository,
                        poi,
                        auth.getCurrentUser().getUid(),
                        (int) rating);

                poiRatingInteractor.execute();
            }
        });
    }

    private void fetchPoiInfo(PoiModel poi) {
        fetchPoiInfo(poi.getId());
    }



    private void setPoiInfo() {

        setPoiMediaSlider(poi);
        setPoiDescriptionText(poi);

        setPoiAddress(poi);
        setPoiRatingBars(poi);
        setPoiContentGrid(poi.getContent(),moreContent);
        getSupportActionBar().setTitle(poi.getName());

    }

    private void setPoiAddress(final PoiModel poi) {
        TextView address = (TextView) findViewById(R.id.address);
        address.setText(poi.getAddress());

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "geo:" + poi.getLatitude() + "," + poi.getLongitude() + "?q=" + poi.getAddress();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(Intent.createChooser(intent, "Select an application"));
            }
        });
    }

    private void fetchPoiInfo(String poiId) {
        TemplateInteractor.CallBack callback = new TemplateInteractor.CallBack<PoiModel>() {

            @Override
            public void onNetworkError() {
                Log.d(TAG,"Network Error");
                mSwipeContainer.setRefreshing(false);
                Toast.makeText(getApplicationContext(),R.string.error_connection, LENGTH_SHORT).show();
            }

            @Override
            public void onError(String code, String message) {
                Log.d(TAG,"Error fetching data");
                mSwipeContainer.setRefreshing(false);
                Toast.makeText(getApplicationContext(),R.string.error_fetch, LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(PoiModel poiInformation) {
                Log.d(TAG,"Fetched data");
                mSwipeContainer.setRefreshing(false);
                poi = poiInformation;

                setPoiInfo();
            }
        };

        String userId = null;
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        PoiDetailInteractorImpl poiDetailInteractor = new PoiDetailInteractorImpl(
            ThreadExecutor.getInstance(),
            MainThreadImpl.getInstance(),
            callback,
            new PoiRepositoryImpl(this),
            new PostRepositoryImpl(this),
            poiId,
            userId);

        poiDetailInteractor.execute();
    }

    private void fetchPoiContent(String poiId) {
        if (!moreContent) {
            return;
        }

        MainThread mainThread = MainThreadImpl.getInstance();
        Executor executor = ThreadExecutor.getInstance();
        PoiRepository poiRepository = new PoiRepositoryImpl(this);
        PoiContentInteractor.CallBack callback = new PoiContentInteractor.CallBack() {

            @Override
            public void onNetworkError() {
                Toast.makeText(PoiDetailActivity.this, getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(String code, String message) {
                Toast.makeText(PoiDetailActivity.this, getResources().getString(R.string.error_request), Toast.LENGTH_SHORT);
            }

            @Override
            public void onSuccess(PoiModel poiInformation, boolean hasMoreContent) {
                poi = poiInformation;
                moreContent = hasMoreContent;

                setPoiContentGrid(poi.getContent(), moreContent);
            }
        };

        String userId = null;
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        PoiContentInteractorImpl poiContentInteractor = new PoiContentInteractorImpl(
                executor,
                mainThread,
                callback,
                poiRepository,
                poi,
                userId,
                contentOffeset,
                CONTENT_LIMIT);

        poiContentInteractor.execute();
    }

    /**
     * Add content to the Content Poi grid.
     * @param contentList List of content to be added to the grid
     * @param moreContent If true, adds View More content button
     */
    private void setPoiContentGrid(ArrayList<ContentModel> contentList, boolean moreContent) {

        if (contentList.isEmpty()) {
            return;
        }

        contentIdList = new ArrayList<>();
        contentResourceList = new ArrayList<>();

        for (ContentModel content : contentList){
            contentIdList.add(content.getId());
            contentResourceList.add(content.getResource());
        }

        ViewMoreGridView.ViewMoreGridViewAdapter adapterView = new ViewMoreGridView.ViewMoreGridViewAdapter(
                PoiDetailActivity.this,
                contentIdList.toArray(new String[contentIdList.size()]),
                contentResourceList,
                moreContent
        );

        gridView = (ViewMoreGridView) findViewById(R.id.grid_view_image_text);
        gridView.setAdapter(adapterView);

        gridView.setOnItemClickListener(new ViewMoreGridView.OnItemClickListener() {

            @Override
            public void onViewMoreItemClick() {
                Toast.makeText(PoiDetailActivity.this, "View More", LENGTH_SHORT).show();
                // TODO: 29/04/2017 Start ViewMoreContentActivity
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Toast.makeText(PoiDetailActivity.this, id+"", LENGTH_SHORT).show();
                // TODO: 29/04/2017 Start ContentActivity
            }
        });
    }

    /**
     * Create POI description field with the description associated to the POI model.
     */
    private void setPoiDescriptionText(PoiModel poi) {
        mDescriptionViewMore.setText(poi.getDescription());
    }

    /**
     * Create POI slider with the pictures associated to the POI model.
     */
    private void setPoiMediaSlider(PoiModel poi) {
        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.removeAllSliders();
        for (Resource image : poi.getPhotos()) {
            image.fetchDownloadUrl(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    String imageURL = task.getResult().toString();
                    TextSliderView textSliderView = new TextSliderView(PoiDetailActivity.this);
                    textSliderView.image(imageURL.toString());

                    sliderShow.addSlider(textSliderView);
                }
            });

        }
    }

    /**
     * Create POI rating bar with the rating associated to the POI model.
     * If a user is authenticated, a second rating bar with the rating
     * attributed to this POI is created.
     */
    private void setPoiRatingBars(final PoiModel poi) {

        poiRatingText = (TextView) findViewById(R.id.poiRating);
        userRatingBar = (RatingBar) findViewById(R.id.userRatingBar);

        setPoiRating(poi);

        if (auth.getCurrentUser() == null) {
            ((ViewGroup) userRatingBar.getParent()).removeView(userRatingBar);
            return;
        }

        userRatingBar.setRating(poi.getUserRating());
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setPoiRating(PoiModel poi) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        poiRatingText.setText(decimalFormat.format(poi.getRating()) + "/5");
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
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