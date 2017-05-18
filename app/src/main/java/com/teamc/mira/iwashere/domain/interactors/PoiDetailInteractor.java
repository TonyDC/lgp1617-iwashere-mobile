package com.teamc.mira.iwashere.domain.interactors;

import com.teamc.mira.iwashere.domain.interactors.base.Interactor;
import com.teamc.mira.iwashere.domain.model.PoiModel;

public interface PoiDetailInteractor extends Interactor {

    interface CallBack {

        void onNetworkFail();

        void onError(String code, String message);

        void onSuccess(PoiModel poi);
    }

    void notifyError(String code, String message);

    void notifyError(String code);

    void notifySuccess(PoiModel poi);
}
