package com.teamc.mira.iwashere.domain.interactors;

import com.teamc.mira.iwashere.domain.interactors.base.BasicInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;

public interface PoiContentInteractor extends BasicInteractor {
    interface CallBack extends BasicInteractor.CallBack{
        void onSuccess(PoiModel poi, boolean hasMoreContent);
    }

    void notifySuccess(PoiModel poi, boolean hasMoreContent);
}
