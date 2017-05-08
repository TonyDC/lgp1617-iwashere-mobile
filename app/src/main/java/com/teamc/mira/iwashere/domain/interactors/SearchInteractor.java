package com.teamc.mira.iwashere.domain.interactors;

import com.teamc.mira.iwashere.domain.interactors.base.Interactor;
import com.teamc.mira.iwashere.domain.model.SearchModel;

/**
 * Created by LukášKonkoľ on 01.05.2017.
 */

public interface SearchInteractor extends Interactor {
    interface CallBack {
        void onSuccess(SearchModel searchModel);

        void onFail(String message);

        void onNetworkError();
    }

    void notifySuccess(SearchModel searchModel);

    void notifyError(String code, String message);
}