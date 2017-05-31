package com.teamc.mira.iwashere.domain.interactors.impl;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.interactors.base.AbstractTemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.domain.repository.remote.RouteRepository;

public class RouteDetailInteractorImpl extends AbstractTemplateInteractor<RouteModel> {
    private RouteModel route;
    private RouteRepository routeRepository;

    public RouteDetailInteractorImpl(Executor threadExecutor, MainThread mainThread, TemplateInteractor.CallBack callBack,
                                     RouteRepository routeRepository,
                                     RouteModel route) {
        super(threadExecutor, mainThread, callBack);

        this.route = route;
        this.routeRepository = routeRepository;
    }

    @Override
    public void run() {
        RouteModel newRoute;
        try {
            newRoute = routeRepository.fetchRoute(route);

            notifySuccess(newRoute);
        } catch (RemoteDataException e) {
            notifyError(e.getCode(),e.getErrorMessage());
        }

    }
}
