package com.teamc.mira.iwashere.domain.interactors;


import com.teamc.mira.iwashere.domain.interactors.base.Interactor;

public interface SignupInteractor extends Interactor {

    interface Callback {
        void onSuccess();

        void onFail(String code, String message);
    }

    void notifyError(final String code, final String message);

    void notifySuccess();
}
