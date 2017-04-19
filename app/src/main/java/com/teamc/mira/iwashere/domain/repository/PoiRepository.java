package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;

public interface PoiRepository {
    PoiModel fetchPoi(String id) throws RemoteDataException;

    PoiModel fetchPoi(PoiModel poi)throws RemoteDataException;

    PoiModel getPoiRating(String poiId, String userId) throws RemoteDataException;

    PoiModel setPOIRating(String poiId, String userId, int newPoiRating) throws RemoteDataException;

    PoiModel setReminder(PoiModel poi)throws RemoteDataException;

    PoiModel removeReminder(PoiModel poi)throws RemoteDataException;
}
