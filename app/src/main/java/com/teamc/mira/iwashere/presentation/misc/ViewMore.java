package com.teamc.mira.iwashere.presentation.misc;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.teamc.mira.iwashere.R;

// TODO: 19/05/2017 Make this inherite from View and create a costum view
public class ViewMore {
    public static final int MAX_LINES = 8;

    private View mView;
    private Context mContext;
    private int mMaxLines = MAX_LINES;
    private String mText;

    public ViewMore(View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    public ViewMore(View mView, Context context, String text) {
        this.mView = mView;
        this.mContext = context;
        this.mText = text;
    }

    public ViewMore(View mView, Context context, String text, int mMaxLines) {
        this.mView = mView;
        this.mContext = context;
        this.mText = text;
        this.mMaxLines = mMaxLines;
    }

    public int getmMaxLines() {
        return mMaxLines;
    }

    public void setmMaxLines(int mMaxLines) {
        this.mMaxLines = mMaxLines;
    }

    public void apply(){
        setText(mText);
        setDynamicDescriptionSize();
    }

    private void setDynamicDescriptionSize() {
        final TextView descriptionText = (TextView) mView.findViewById(R.id.viewMoreText);
        descriptionText.setMaxLines(MAX_LINES);

        final TextView readMore = (TextView) mView.findViewById(R.id.viewMoreButton);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionText.getMaxLines() != Integer.MAX_VALUE) {
                    descriptionText.setMaxLines(Integer.MAX_VALUE);
                    readMore.setText(Html.fromHtml(mContext.getString(R.string.less_info)));
                } else {
                    descriptionText.setMaxLines(MAX_LINES);
                    readMore.setText(Html.fromHtml(mContext.getString(R.string.more_info)));
                }
            }
        });
    }

    public void setText(String text) {
        TextView textDescription = (TextView) mView.findViewById(R.id.viewMoreText);
        textDescription.setText(text);
    }}
