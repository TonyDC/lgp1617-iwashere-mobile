package com.teamc.mira.iwashere.util;

import android.content.Context;

/**
 * Created by Duart on 03/05/2017.
 */

public class StorateGridViewAdapter extends GridViewAdapter {
    public StorateGridViewAdapter(Context context, String[] gridViewString, String[] gridViewImageUrl) {
        super(context, gridViewString, gridViewImageUrl);
    }

    public StorateGridViewAdapter(Context context, String[] gridViewString, String[] gridViewImageUrl, int mTemplateImageId) {
        super(context, gridViewString, gridViewImageUrl, mTemplateImageId);
    }
}
