package com.teamc.mira.iwashere.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.teamc.mira.iwashere.R;

public class GridViewAdapter extends BaseAdapter {
    int mTemplateImageId = R.mipmap.default_thumbnail;
    Context mContext;
    String[] mGridViewString;
    String[] mGridViewImageUrl;

    public GridViewAdapter(Context context, String[] gridViewString, String[] gridViewImageUrl) {
        mContext = context;
        this.mGridViewImageUrl = gridViewImageUrl;
        this.mGridViewString = gridViewString;
    }

    public GridViewAdapter(Context context, String[] gridViewString, String[] gridViewImageUrl, int mTemplateImageId) {
        this(context,gridViewString, gridViewImageUrl);
        this.mTemplateImageId = mTemplateImageId;


    }

    @Override
    public int getCount() {
        return mGridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = inflater.inflate(R.layout.activity_poi_grid_item, null);
            ImageView imageView = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);

            imageView.setImageResource(mTemplateImageId);


            GridView
            Picasso.with(this.mContext).load(mGridViewImageUrl[i])
                    .into(imageView);

        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }
}