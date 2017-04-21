package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;

import org.json.JSONObject;

public interface PoiRepository {
    /**
     * Fetch a POI's information through API.
     * @param poiId the POI's id
     * @return PoiModel with information about the POI
     * @throws RemoteDataException
     * @see PoiModel( JSONObject )
     */
    PoiModel fetchPoi(String poiId) throws RemoteDataException;

    /**
     * Fetch a POI's rating through API.
     * @param poiId the POI's id
     * @return PoiModel with the POI's rating and rating count
     * @throws RemoteDataException
     */
    PoiModel fetchPoiRating(String poiId) throws RemoteDataException;

    /**
     * Fetch the rating attributed by a user to a POI through API.
     * @param poiId the POI's id
     * @param userId the user's id
     * @return PoiModel with the user's rating
     * @throws RemoteDataException
     */
    PoiModel fetchPoiUserRating(String poiId, String userId) throws RemoteDataException;

    /**
     * Save the rating attributed by a user to a POI through API.
     * Return the POI's updated rating through API.
     * @param poiId the POI's id
     * @param userId the user's id
     * @param newPoiRating the rating the user attributed to the POI
     * @return PoiModel with the user's rating
     * @throws RemoteDataException
     * @see #fetchPoiRating( String )
     */
    PoiModel setPoiUserRating(String poiId, String userId, int newPoiRating) throws RemoteDataException;

    PoiModel setReminder(PoiModel poi)throws RemoteDataException;

    PoiModel removeReminder(PoiModel poi)throws RemoteDataException;
}
