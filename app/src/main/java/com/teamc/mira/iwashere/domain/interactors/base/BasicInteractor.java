package com.teamc.mira.iwashere.domain.interactors.base;

public interface BasicInteractor extends Interactor {
    interface CallBack{
        void onNetworkError();

        void onError(String code, String message);
    }

    void notifyError(String code, String message);
}
