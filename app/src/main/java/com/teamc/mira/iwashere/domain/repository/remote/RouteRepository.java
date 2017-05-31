package com.teamc.mira.iwashere.domain.repository.remote;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.RouteModel;

public interface RouteRepository {

    RouteModel fetchRoute(RouteModel route) throws RemoteDataException;

    RouteModel fetchRoute(String routeId) throws RemoteDataException;

    float fetchRating(String routeId);

    float fetchUsersRating(String routeId);

    float fetchRoutesPois(String routeId);
}
