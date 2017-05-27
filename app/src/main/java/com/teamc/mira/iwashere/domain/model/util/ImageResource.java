package com.teamc.mira.iwashere.domain.model.util;

import com.google.android.gms.tasks.OnCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageResource extends Resource {

    String storageXS;
    String storageS;
    String storageM;
    String storageL;

    protected ImageResource() {
        super(Type.IMAGE);
    }

    protected ImageResource(JSONObject jsonObject) throws JSONException {
        super(Type.IMAGE);

        this.storageL = jsonObject.getString("urlL");
        this.storageM = jsonObject.getString("urlM");
        this.storageS = jsonObject.getString("urlS");
        this.storageXS = jsonObject.getString("urlXs");
    }

    public enum Size {
        SIZE_XSMALL,
        SIZE_SMALL,
        SIZE_MEDIUM,
        SIZE_LARGE
    }

    public void fetchDownloadUrl(Size size, OnCompleteListener listener) {
        String storageUrl;
        switch (size){
            case SIZE_XSMALL:
                storageUrl = storageXS;

                break;

            case SIZE_SMALL:
                storageUrl = storageS;

                break;
            case SIZE_MEDIUM:
                storageUrl = storageM;

                break;
            case SIZE_LARGE:
                storageUrl = storageL;
                break;
            default:
                storageUrl = storageL;
        }

        fetchDownloadUrl(listener, storageUrl);
    }



    @Deprecated
    public void fetchDownloadUrl(OnCompleteListener listener) {
        fetchDownloadUrl(listener, storageL);
    }
}
