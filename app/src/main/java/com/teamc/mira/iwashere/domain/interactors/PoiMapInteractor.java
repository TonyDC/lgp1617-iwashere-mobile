package com.teamc.mira.iwashere.domain.interactors;

import com.teamc.mira.iwashere.domain.interactors.base.Interactor;
import com.teamc.mira.iwashere.domain.model.PoiModel;

import java.util.ArrayList;

/**
 * Created by Duart on 16/04/2017.
 */

public interface PoiMapInteractor extends Interactor {
    interface CallBack {
        void onSuccess(ArrayList<PoiModel> poiModels);

        void onFail(String message);

        void onNetworkError();
    }

    void notifySuccess(ArrayList<PoiModel> poiModels);

    void notifyError(String code, String message);

}
