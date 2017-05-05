package com.teamc.mira.iwashere.domain.repository;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.SearchModel;

/**
 * Created by LukášKonkoľ on 30.04.2017.
 */

public interface SearchRepository {
    SearchModel search(String searchQuery, double lat, double lng) throws RemoteDataException;
}