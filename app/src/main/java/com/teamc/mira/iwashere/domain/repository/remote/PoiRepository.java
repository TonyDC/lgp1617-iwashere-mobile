package com.teamc.mira.iwashere.domain.repository.remote;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;

import java.util.ArrayList;
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
     * Fetch a POI's media list through API.
     * @param poi the PoiModel with information about the POI
     * @return true upon success, false otherwise
     * @throws RemoteDataException
     * @see PoiModel( JSONObject )
     */
    boolean fetchPoiMedia(PoiModel poi) throws RemoteDataException;

    /**
     * Fetch a POI's rating through API.
     * @param poi the PoiModel with information about the POI
     * @return true upon success, false otherwise
     * @throws RemoteDataException
     */
    boolean fetchPoiRating(PoiModel poi) throws RemoteDataException;

    /**
     * Fetch the rating attributed by a user to a POI through API, updating the PoiModel.
     * @param poi the PoiModel with information about the POI
     * @param userId the user's id
     * @return true upon success, false otherwise
     * @throws RemoteDataException
     */
    boolean fetchPoiUserRating(PoiModel poi, String userId) throws RemoteDataException;

    /**
     * Save the rating attributed by a user to a POI through API.
     * Update the POI's rating through API in the PoiModel.
     * @param poi the PoiModel with information about the POI
     * @param userId the user's id
     * @param newPoiRating the rating the user attributed to the POI
     * @return true upon success, false otherwise
     * @throws RemoteDataException
     * @see #fetchPoiRating( PoiModel )
     */
    boolean setPoiUserRating(PoiModel poi, String userId, int newPoiRating) throws RemoteDataException;

    /**
     * Fetch the Content associated to the POI, updating the PoiModel.
     * @param poi the PoiModel with information about the POI
     * @param userId the user's id
     * @param contentOffset
     * @param contentLimit
     * @return true upon success, false otherwise
     * @throws RemoteDataException
     */
    boolean fetchPoiContent(PoiModel poi, String userId, int contentOffset, int contentLimit) throws RemoteDataException;

    PoiModel setReminder(PoiModel poi)throws RemoteDataException;

    PoiModel removeReminder(PoiModel poi)throws RemoteDataException;

    ArrayList<PoiModel> fetchPoisInArea(double maxLat, double minLat, double maxLong, double minLong) throws RemoteDataException;

    ArrayList<PoiModel> searchPois(String searchQuery);
}
