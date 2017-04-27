package com.teamc.mira.iwashere.presentation.poi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.PoiRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiDetailInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiRatingInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PoiDetailActivity extends AppCompatActivity {

    public static final String TAG = PoiDetailActivity.class.getSimpleName();
    public static final int MAX_LINES = 8;

    PoiModel poi;
    FirebaseAuth auth;

    /* View components */
    SliderLayout sliderShow;
    TextView textDescription, addressT, addressF, hoursT, hoursF;
    ImageView pinPoint;
    RatingBar userRatingBar;
    GridView photoGallery;


    GridView gridView;
    String[] gridViewString = {
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",

    };

    int[] gridViewImageId = {
            R.drawable.logo, R.drawable.ic_location_on_black_32dp, R.drawable.logo, R.drawable.ic_location_on_black_32dp, R.drawable.logo, R.drawable.ic_location_on_black_32dp,
            R.drawable.logo, R.drawable.ic_location_on_black_32dp, R.drawable.logo, R.drawable.ic_location_on_black_32dp, R.drawable.logo, R.drawable.ic_location_on_black_32dp,
            R.drawable.logo, R.drawable.ic_location_on_black_32dp, R.drawable.logo, R.drawable.ic_location_on_black_32dp, R.drawable.logo, R.drawable.ic_location_on_black_32dp,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        auth = FirebaseAuth.getInstance();

        setToolBar();
        setPoiInfoTemp();


        final TextView descriptionText = (TextView) findViewById(R.id.description);
        descriptionText.setMaxLines(MAX_LINES);

        final TextView readMore = (TextView) findViewById(R.id.moreInformation);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionText.getMaxLines() != Integer.MAX_VALUE) {
                    descriptionText.setMaxLines(Integer.MAX_VALUE);
                    readMore.setText(Html.fromHtml(getString(R.string.more_info)));

                } else {
                    descriptionText.setMaxLines(MAX_LINES);
                    readMore.setText(Html.fromHtml(getString(R.string.less_info)));
                }
            }
        });

    }

    private void setPoiInfoTemp() {
        // for test purposes; TODO replace by API interactor


        URL url1 = null, url2 = null;
        ArrayList<URL> urls = new ArrayList<URL>();
        try {
            url1 = new URL("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
            url2 = new URL("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
            urls.add(url1);
            urls.add(url2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        poi = new PoiModel("1", "poi name", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam sodales suscipit venenatis. Sed ut rhoncus mi. Curabitur nec scelerisque ipsum. Fusce et diam eros. Pellentesque vel dolor ante. Suspendisse convallis diam nec eleifend tincidunt. Etiam vestibulum mi elit. Sed egestas tellus mattis, fermentum turpis eu, gravida neque. Sed at turpis ultricies, laoreet purus sed, sollicitudin urna. Donec diam ex, porta quis sollicitudin id, sollicitudin id urna. Nunc commodo rutrum odio sit amet viverra. Vestibulum blandit euismod efficitur. Nunc sit amet hendrerit enim. Pellentesque id diam lacus. Etiam vitae tellus sed turpis suscipit laoreet. Integer commodo in nulla nec vehicula.", "Street noname, 4200-008 Porto", "longitude", "latitude",
                urls, null, null);
        poi.setRating(4);
        poi.setUserRating(1);

        setPoiSlider();
        setPoiDescriptionText();
        setPoiRatingBars();
        setPoiContentGrid();
        getSupportActionBar().setTitle(poi.getName());

    }

    private void fetchPoiInfo(String poiId) {
        MainThread mainThread = MainThreadImpl.getInstance();
        Executor executor = ThreadExecutor.getInstance();
        PoiRepository poiRepository = new PoiRepositoryImpl(this);
        PoiDetailInteractor.CallBack callback = new PoiDetailInteractor.CallBack() {

            @Override
            public void onNetworkFail() {
                onError(null, null);
            }

            @Override
            public void onError(String code, String message) {
                // TODO: redirect to previous display?
            }

            @Override
            public void onSuccess(PoiModel poiInformation) {
                poi = poiInformation;

                setPoiSlider();
                setPoiDescriptionText();
                setPoiRatingBars();
                //setPoiContentGrid();
            }
        };

        String userId = null;
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        setPoiContentGrid();


        PoiDetailInteractor poiDetailInteractor = new PoiDetailInteractorImpl(
                executor,
                mainThread,
                callback,
                poiRepository,
                poiId,
                userId);

        poiDetailInteractor.execute();
    }

    // TODO: improve this
    private void setPoiContentGrid() {
        GridViewAdapter adapterView = new GridViewAdapter(PoiDetailActivity.this, gridViewString, gridViewImageId);
        gridView = (GridView) findViewById(R.id.grid_view_image_text);
        gridView.setAdapter(adapterView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Toast.makeText(PoiDetailActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Create POI description field with the description associated to the POI model.
     */
    private void setPoiDescriptionText() {
        textDescription = (TextView) findViewById(R.id.description);
        textDescription.setText(poi.getDescription());
    }

    /**
     * Create POI slider with the pictures associated to the POI model.
     */
    private void setPoiSlider() {
        sliderShow = (SliderLayout) findViewById(R.id.slider);

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
    private void setPoiRatingBars() {

        userRatingBar = (RatingBar) findViewById(R.id.userRatingBar);

        setPoiRating();

        if (auth.getCurrentUser() == null) {
            ((ViewGroup) userRatingBar.getParent()).removeView(userRatingBar);

            return;
        }

        final Context thisContext = this;

        userRatingBar.setRating(poi.getUserRating());
        userRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, final float rating,
                                        boolean fromUser) {

                if (!fromUser) {
                    return;
                }

                MainThread mainThread = MainThreadImpl.getInstance();
                Executor executor = ThreadExecutor.getInstance();
                PoiRepository poiRepository = new PoiRepositoryImpl(thisContext);
                PoiDetailInteractor.CallBack callback = new PoiDetailInteractor.CallBack() {

                    @Override
                    public void onNetworkFail() {
                        onError(null, null);
                    }

                    @Override
                    public void onError(String code, String message) {
                        userRatingBar.setRating(poi.getUserRating());
                    }

                    @Override
                    public void onSuccess(PoiModel updatedPoi) {
                        poi.setUserRating(rating);
                        poi.setRating(updatedPoi.getRating());
                        setPoiRating();
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

    private void setPoiRating() {

    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

}