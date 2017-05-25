package com.teamc.mira.iwashere.presentation.misc.costum_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamc.mira.iwashere.R;

public class ViewMore extends LinearLayout{
    public static final int MAX_LINES = 8;
    private static final String TAG = ViewMore.class.getSimpleName();
    private final TextView viewMoreButton;

    private View mView;
    private Context mContext;
    private int mMaxLines = MAX_LINES;
    private String mText;
    private final TextView textView;

    private final String attrText;
    private final int attrMaxLines;

    public ViewMore(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.view_more_text, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ViewMore, 0, 0);

        // Store attr arguments
        attrText = a.getString(R.styleable.ViewMore_text);
        attrMaxLines = a.getInt(R.styleable.ViewMore_maxLines, MAX_LINES);


        // Get views
        textView = (TextView) getChildAt(0);
        viewMoreButton = (TextView) getChildAt(1);

        // Assign attr arguments as initial arguments
        setText(attrText);
        setMaxLines(attrMaxLines);

        setText(mText);
        setDynamicDescriptionSize();

        // TODO: 21/05/2017 change text color and view more button color dynamically
    }

    public int getMaxLines() {
        return mMaxLines;
    }

    public void setMaxLines(int mMaxLines) {
        this.mMaxLines = mMaxLines;
    }

    private void applyViewMoreButtonDynamicVisibility() {
        Layout l = textView.getLayout();
        if ((l.getLineCount() > 0 && l.getEllipsisCount(l.getLineCount() - 1) > 0 ) ||
                textView.getLineCount() > mMaxLines) {
            viewMoreButton.setVisibility(VISIBLE);
        } else {
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
        if(text != null && text.trim().length() > 0){
            textDescription.setText(text);
        }else{
            textView.setText(attrText);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        applyViewMoreButtonDynamicVisibility();
    }
}
