package com.teamc.mira.iwashere.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;
import com.teamc.mira.iwashere.R;

public class ViewMoreGridView extends ExpandableHeightGridView {
    OnItemClickListener mOnItemClickListener;


    public static class ViewMoreGridViewAdapter extends GridViewAdapter {
        int mViewMoreImageId = R.mipmap.ic_add;
        static final String VIEW_MORE_ITEM_ID = "viewMore";
        static final String VIEW_MORE_ITEM_URL = "viewMoreUrl";

        public ViewMoreGridViewAdapter(Context context, String[] gridViewString, String[] gridViewImageUrl) {
            super(context, gridViewString, gridViewImageUrl);
            add(VIEW_MORE_ITEM_ID, VIEW_MORE_ITEM_URL);
        }

        public ViewMoreGridViewAdapter(Context context, String[] gridViewString, String[] gridViewImageUrl, int mTemplateImageId) {
            super(context, gridViewString, gridViewImageUrl, mTemplateImageId);
            add(VIEW_MORE_ITEM_ID, VIEW_MORE_ITEM_URL);
        }

        private void add(String id, String url){
            String[] newGridViewString = new String[mGridViewString.length +1];
            System.arraycopy(mGridViewString,0,newGridViewString, 0, mGridViewString.length);
            newGridViewString[mGridViewString.length] = id;
            mGridViewString = newGridViewString;

            String[] newGridViewUrl = new String[mGridViewImageUrl.length+1];
            System.arraycopy(mGridViewImageUrl, 0, newGridViewUrl, 0, mGridViewImageUrl.length);
            newGridViewUrl[mGridViewImageUrl.length] = url;
            mGridViewImageUrl = newGridViewUrl;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if(i==getCount()-1){
                View gridViewAndroid;
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {

                    gridViewAndroid = inflater.inflate(R.layout.activity_poi_grid_item, null);
                    ImageView imageView = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);

                    imageView.setImageResource(mViewMoreImageId);

                } else {
                    gridViewAndroid = (View) convertView;
                }

                return gridViewAndroid;
            }else{
                return super.getView(i, convertView, parent);
            }
        }
    }

    public ViewMoreGridView(Context context) {
        super(context);
        enableViewMore();
    }

    public ViewMoreGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        enableViewMore();
    }

    public ViewMoreGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        enableViewMore();
    }

    private void enableViewMore(){
        OnItemClickListener onItemClickListener = mOnItemClickListener;

        super.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getAdapter().getCount() == position+1){
                    mOnItemClickListener.onViewMoreItemClick();
                }else{
                    mOnItemClickListener.onItemClick(parent,view,position,id);
                }
            }
        });
    }

    public interface OnItemClickListener extends AdapterView.OnItemClickListener {
        void onViewMoreItemClick();
    }

    @Deprecated
    @Override
    public void setOnItemClickListener(@Nullable AdapterView.OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
