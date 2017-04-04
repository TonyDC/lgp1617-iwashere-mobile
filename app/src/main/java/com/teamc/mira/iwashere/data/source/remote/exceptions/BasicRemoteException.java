package com.teamc.mira.iwashere.data.source.remote.exceptions;

/**
 * Created by Duart on 03/04/2017.
 */

public class BasicRemoteException extends RemoteDataException {
    public BasicRemoteException(String code, String errorMessage) {
        super(code, errorMessage);
    }

    public BasicRemoteException(String code) {
        super(code);
    }
}
