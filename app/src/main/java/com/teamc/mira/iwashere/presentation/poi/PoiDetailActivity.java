package com.teamc.mira.iwashere.presentation.poi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.PoiRepositoryImpl;
import com.teamc.mira.iwashere.data.source.remote.UserRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.AuthInteractor;
import com.teamc.mira.iwashere.domain.interactors.PoiDetailInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiDetailInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.PoiRatingInteractorImpl;
import com.teamc.mira.iwashere.domain.interactors.impl.SignupInteractorImpl;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.UserRepository;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

import java.text.DecimalFormat;

public class PoiDetailActivity extends AppCompatActivity {

    PoiModel poi;
    SliderLayout sliderShow;
    TextView textDescription;
    ImageView textAddress;
    GridView gridView;
    FirebaseAuth auth;

    String[] gridViewString = {
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",
            "Alram", "Android", "Mobile", "Website", "Profile", "WordPress",

    } ;
    GridView photoGallery;
    RatingBar poiRatingBar;
    TextView poiRatingText;
    RatingBar userRatingBar;

    int[] gridViewImageId = {
            R.drawable.logo, R.drawable.place, R.drawable.logo, R.drawable.place, R.drawable.logo, R.drawable.place,
            R.drawable.logo, R.drawable.place, R.drawable.logo, R.drawable.place, R.drawable.logo, R.drawable.place,
            R.drawable.logo, R.drawable.place, R.drawable.logo, R.drawable.place, R.drawable.logo, R.drawable.place,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sliderShow = (SliderLayout) findViewById(R.id.slider);

        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .image("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        sliderShow.addSlider(textSliderView);

        textDescription = (TextView) findViewById(R.id.description);
        textDescription.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam sodales suscipit venenatis. Sed ut rhoncus mi. Curabitur nec scelerisque ipsum. Fusce et diam eros. Pellentesque vel dolor ante. Suspendisse convallis diam nec eleifend tincidunt. Etiam vestibulum mi elit. Sed egestas tellus mattis, fermentum turpis eu, gravida neque. Sed at turpis ultricies, laoreet purus sed, sollicitudin urna. Donec diam ex, porta quis sollicitudin id, sollicitudin id urna. Nunc commodo rutrum odio sit amet viverra. Vestibulum blandit euismod efficitur. Nunc sit amet hendrerit enim. Pellentesque id diam lacus. Etiam vitae tellus sed turpis suscipit laoreet. Integer commodo in nulla nec vehicula.");

        textAddress = (ImageView) findViewById(R.id.address);

        textAddress.setBackgroundColor(Color.parseColor("#35A8DF"));

        GridViewAdapter adapterView = new GridViewAdapter(PoiDetailActivity.this, gridViewString, gridViewImageId);
        gridView=(GridView)findViewById(R.id.grid_view_image_text);
        gridView.setAdapter(adapterView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Toast.makeText(PoiDetailActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });

        auth = FirebaseAuth.getInstance();

        initRatingBars();
    }

    public void initRatingBars() {

        poiRatingBar = (RatingBar) findViewById(R.id.poiRatingBar);

        userRatingBar = (RatingBar) findViewById(R.id.userRatingBar);
        poiRatingText = (TextView) findViewById(R.id.poiRatingText);

        poiRatingBar.setIsIndicator(true);
        setPoiRatingText();
        setRatingBarsStyle();

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
                        setPoiRatingText();
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

    private void setPoiRatingText() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.format(poi.getRating());
        poiRatingText.setText(decimalFormat + "/5");
    }

    private void setRatingBarsStyle() {
        findViewById(R.id.ratings).setBackgroundColor(Color.parseColor("#35A8DF"));
        poiRatingText.setTextColor(Color.BLACK);
        poiRatingBar.setBackgroundColor(Color.parseColor("#35A8DF"));
        userRatingBar.setBackgroundColor(Color.parseColor("#35A8DF"));
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

}