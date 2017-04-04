package com.teamc.mira.iwashere.data.source.remote.exceptions;

/**
 * Created by Duart on 03/04/2017.
 */

public abstract class RemoteDataException extends Exception {
    private String  code;

    public RemoteDataException(String code, String errorMessage) {
        super(errorMessage);
        this.code = code;
    }

    public RemoteDataException(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage(){
        return super.getMessage();
    }

    void setCode(String code) {
        this.code = code;
    }
}
