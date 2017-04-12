package com.teamc.mira.iwashere.data.source.remote;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

// TODO: 12/04/2017 Implement functions
public class PoiRepositoryImpl implements PoiRepository {
    @Override
    public PoiModel fetchPoi(String id) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel fetchPoi(PoiModel poi) throws RemoteDataException {
        return fetchPoi(poi.getId());
    }

    @Override
    public PoiModel setReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PoiModel removeReminder(PoiModel poi) throws RemoteDataException {
        throw new UnsupportedOperationException();
    }
}
