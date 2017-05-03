package com.teamc.mira.iwashere.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.util.Resource;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    int mTemplateImageId = R.mipmap.default_thumbnail;
    Context mContext;
    String[] mGridViewString;
    ArrayList<Resource> mResources;

    public GridViewAdapter(Context context, String[] gridViewString, ArrayList<Resource> resources) {
        mContext = context;
        this.mResources = resources;
        this.mGridViewString = gridViewString;
    }

    public GridViewAdapter(Context context, String[] gridViewString, ArrayList<Resource> resources, int mTemplateImageId) {
        this(context,gridViewString, resources);
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
    public View getView(final int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = inflater.inflate(R.layout.activity_poi_grid_item, null);
            final ImageView imageView = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);

            imageView.setImageResource(mTemplateImageId);

            Resource resource = mResources.get(i);
            resource.fetchDownloadUrl(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Picasso.with(mContext).load(task.getResult().toString())
                        .into(imageView);
                }
            });


        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }
}