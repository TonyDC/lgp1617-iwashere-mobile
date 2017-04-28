package com.teamc.mira.iwashere.domain.interactors;

import com.teamc.mira.iwashere.domain.interactors.base.Interactor;
import com.teamc.mira.iwashere.domain.model.PoiModel;

public interface PoiContentInteractor extends Interactor {

    interface CallBack {

        void onNetworkFail();

        void onError(String code, String message);

        void onSuccess(PoiModel poi, boolean moreResults);
    }

    void notifyError(String code, String message);

    void notifyError(String code);

    void notifySuccess(PoiModel poi);
}
