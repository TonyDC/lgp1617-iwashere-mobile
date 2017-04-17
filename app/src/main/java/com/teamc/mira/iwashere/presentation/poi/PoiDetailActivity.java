package com.teamc.mira.iwashere.presentation.poi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.teamc.mira.iwashere.R;

/**
 * Created by Duart on 12/04/2017.
 */

public class PoiDetailActivity extends AppCompatActivity {

    SliderLayout sliderShow;
    TextView textDescription;
    ImageView textAddress;
    GridView photoGallery;
    RatingBar poiRatingBar;
    TextView poiRatingText;
    RatingBar userRatingBar;


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
                .description("Game of Thrones")
                .image("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        sliderShow.addSlider(textSliderView);

        textDescription = (TextView) findViewById(R.id.description);
        textDescription.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam sodales suscipit venenatis. Sed ut rhoncus mi. Curabitur nec scelerisque ipsum. Fusce et diam eros. Pellentesque vel dolor ante. Suspendisse convallis diam nec eleifend tincidunt. Etiam vestibulum mi elit. Sed egestas tellus mattis, fermentum turpis eu, gravida neque. Sed at turpis ultricies, laoreet purus sed, sollicitudin urna. Donec diam ex, porta quis sollicitudin id, sollicitudin id urna. Nunc commodo rutrum odio sit amet viverra. Vestibulum blandit euismod efficitur. Nunc sit amet hendrerit enim. Pellentesque id diam lacus. Etiam vitae tellus sed turpis suscipit laoreet. Integer commodo in nulla nec vehicula.");

        textAddress = (ImageView) findViewById(R.id.address);

        textAddress.setBackgroundColor(Color.parseColor("#35A8DF"));

        photoGallery = (GridView) findViewById(R.id.photogallery);
        photoGallery.setAdapter(new ImageAdapter(this));

        photoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(PoiDetailActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        initRatingBars();
    }

    public void initRatingBars() {

        poiRatingBar = (RatingBar) findViewById(R.id.poiRatingBar);
        userRatingBar = (RatingBar) findViewById(R.id.userRatingBar);

        poiRatingText = (TextView) findViewById(R.id.poiRatingText);
        poiRatingText.setText(" x.x /5");
        poiRatingText.setTextColor(Color.BLACK);

        findViewById(R.id.ratings).setBackgroundColor(Color.parseColor("#35A8DF"));
        poiRatingBar.setBackgroundColor(Color.parseColor("#35A8DF"));
        userRatingBar.setBackgroundColor(Color.parseColor("#35A8DF"));

        poiRatingBar.setIsIndicator(true);

        userRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                // TODO call API for rating update and update bars appropriately
            }
        });
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

}
