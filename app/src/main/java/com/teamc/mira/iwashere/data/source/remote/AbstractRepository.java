package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;

import com.teamc.mira.iwashere.domain.repository.Repository;

/**
 * Created by Duart on 16/04/2017.
 */

public class AbstractRepository {

    Context mContext;

    public AbstractRepository(Context mContext) {
        this.mContext = mContext;
    }
}
