package com.teamc.mira.iwashere.domain.interactors.base;

public interface TemplateInteractor<T> extends BasicInteractor{
    interface CallBack<T> extends BasicInteractor.CallBack {
        void onSuccess(T result);
    }

    void notifySuccess(T result);
}
