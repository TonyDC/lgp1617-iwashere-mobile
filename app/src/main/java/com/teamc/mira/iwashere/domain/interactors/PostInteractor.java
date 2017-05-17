package com.teamc.mira.iwashere.domain.interactors;


import com.teamc.mira.iwashere.domain.interactors.base.Interactor;
import com.teamc.mira.iwashere.domain.model.PoiModel;

public interface PostInteractor extends Interactor {

    interface Callback {
        void onNetworkFail();

        void onError(String code, String message);

        void onSuccess(PoiModel poi, boolean moreResults);
    }

    void notifyError(final String code, final String message);

    void notifySuccess();
}
