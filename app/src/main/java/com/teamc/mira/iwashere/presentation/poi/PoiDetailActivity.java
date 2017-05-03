package com.teamc.mira.iwashere.presentation.poi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.impl.PostRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PoiContentInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiContentInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiDetailInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiRatingInteractorImpl;
import com.teamc.mira.iwashere.domain.model.ContentModel;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.remote.PoiRepository;
import com.teamc.mira.iwashere.threading.MainThreadImpl;
import com.teamc.mira.iwashere.util.ExpandableHeightGridView;
import com.teamc.mira.iwashere.util.ViewMoreGridView;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class PoiDetailActivity extends AppCompatActivity {

    public static final String TAG = PoiDetailActivity.class.getSimpleName();
    public static final int MAX_LINES = 8;
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
    private ArrayList<String> contentUrlList;
    private SwipeRefreshLayout mSwipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        auth = FirebaseAuth.getInstance();

        setToolBar();
        poi = (PoiModel) getIntent().getSerializableExtra(POI);
        setPoiInfo();
        setDynamicDescriptionSize();

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
                PoiDetailInteractor.CallBack callback = new PoiDetailInteractor.CallBack() {

                    @Override
                    public void onNetworkFail() {
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

                PoiDetailInteractor poiRatingInteractor = new PoiRatingInteractorImpl(
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

    private void setDynamicDescriptionSize() {
        final TextView descriptionText = (TextView) findViewById(R.id.description);
        descriptionText.setMaxLines(MAX_LINES);

        final TextView readMore = (TextView) findViewById(R.id.moreInformation);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionText.getMaxLines() != Integer.MAX_VALUE) {
                    descriptionText.setMaxLines(Integer.MAX_VALUE);
                    readMore.setText(Html.fromHtml(getString(R.string.less_info)));

                } else {
                    descriptionText.setMaxLines(MAX_LINES);
                    readMore.setText(Html.fromHtml(getString(R.string.more_info)));
                }
            }
        });
    }

    private void setPoiInfo() {

        setPoiMediaSlider(poi);
        setPoiDescriptionText(poi);
        setPoiRatingBars(poi);
        setPoiContentGrid(poi.getContent(),moreContent);
        getSupportActionBar().setTitle(poi.getName());

    }

    private void fetchPoiInfo(String poiId) {
        PoiDetailInteractor.CallBack callback = new PoiDetailInteractor.CallBack() {

            @Override
            public void onNetworkFail() {
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

        PoiDetailInteractor poiDetailInteractor = new PoiDetailInteractorImpl(
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
            public void onNetworkFail() {
                onError(null, null);
            }

            @Override
            public void onError(String code, String message) {
                // TODO: redirect to previous display?
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

        PoiContentInteractor poiContentInteractor = new PoiContentInteractorImpl(
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
        contentUrlList = new ArrayList<>();


        for (ContentModel content : contentList){
            contentIdList.add(content.getId());
            contentUrlList.add(content.getUrl().toString());
        }

        ViewMoreGridView.ViewMoreGridViewAdapter adapterView = new ViewMoreGridView.ViewMoreGridViewAdapter(
                PoiDetailActivity.this,
                contentIdList.toArray(new String[contentIdList.size()]),
                contentUrlList.toArray(new String[contentUrlList.size()]),
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
        TextView textDescription = (TextView) findViewById(R.id.description);
        textDescription.setText(poi.getDescription());
    }

    /**
     * Create POI slider with the pictures associated to the POI model.
     */
    private void setPoiMediaSlider(PoiModel poi) {
        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.removeAllSliders();
        for (URL imageURL : poi.getPhotos()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.image(imageURL.toString());

            sliderShow.addSlider(textSliderView);
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

}