package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;

public interface PoiRepository {
    PoiModel fetchPoi(String id) throws RemoteDataException;

    PoiModel fetchPoi(PoiModel poi)throws RemoteDataException;

    PoiModel fetchPoiRating(String id) throws RemoteDataException;

    PoiModel fetchPoiRating(PoiModel poi) throws RemoteDataException;

    PoiModel setReminder(PoiModel poi)throws RemoteDataException;

    PoiModel removeReminder(PoiModel poi)throws RemoteDataException;


}
