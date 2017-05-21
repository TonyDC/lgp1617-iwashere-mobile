package com.teamc.mira.iwashere.presentation.misc.costum_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamc.mira.iwashere.R;

// TODO: 19/05/2017 Make this inherite from View and create a costum view
public class ViewMore extends LinearLayout{
    public static final int MAX_LINES = 8;
    private final TextView viewMoreButton;

    private View mView;
    private Context mContext;
    private int mMaxLines = MAX_LINES;
    private String mText;
    private final TextView textView;

    public ViewMore(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_view_more_text, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ViewMore, 0, 0);
        mText = a.getString(R.styleable.ViewMore_text);
        mMaxLines = a.getInt(R.styleable.ViewMore_maxLines, MAX_LINES);

        textView = (TextView) getChildAt(0);
        textView.setText(mText);

        viewMoreButton = (TextView) getChildAt(1);
        setDynamicDescriptionSize();
        applyViewMoreButtonDynamicVisibility();

        // TODO: 21/05/2017 change text color and view more button color dynamically
    }

    public int getmMaxLines() {
        return mMaxLines;
    }

    public void setmMaxLines(int mMaxLines) {
        this.mMaxLines = mMaxLines;
    }

    /*private void apply(){
        setText(mText);
        setDynamicDescriptionSize();
        applyViewMoreButtonDynamicVisibility();
    }*/

    private void applyViewMoreButtonDynamicVisibility() {
        if(textView.getLineCount() <= mMaxLines){
            viewMoreButton.setVisibility(GONE);
        }
    }

    private void setDynamicDescriptionSize() {
        textView.setMaxLines(mMaxLines);

        final TextView readMore = (TextView) mView.findViewById(R.id.viewMoreButton);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getMaxLines() != Integer.MAX_VALUE) {
                    textView.setMaxLines(Integer.MAX_VALUE);
                    readMore.setText(Html.fromHtml(mContext.getString(R.string.less_info)));
                } else {
                    textView.setMaxLines(MAX_LINES);
                    readMore.setText(Html.fromHtml(mContext.getString(R.string.more_info)));
                }
            }
        });
    }

    public void setText(String text) {
        TextView textDescription = (TextView) mView.findViewById(R.id.viewMoreText);
        textDescription.setText(text);
        applyViewMoreButtonDynamicVisibility();
    }
}
