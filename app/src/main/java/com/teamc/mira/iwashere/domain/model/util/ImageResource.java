package com.teamc.mira.iwashere.domain.model.util;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageResource extends Resource {

    String storageXS;
    String storageS;
    String storageM;
    String storageL;

    Task<Uri> taskXS;
    Task<Uri> taskS;
    Task<Uri> taskM;
    Task<Uri> taskL;

    protected ImageResource() {
        super(Type.IMAGE);
    }

    protected ImageResource(JSONObject jsonObject) throws JSONException {
        super(Type.IMAGE);

        this.storageL = jsonObject.getString("urlL");
        this.storageM = jsonObject.getString("urlM");
        this.storageS = jsonObject.getString("urlS");
        this.storageXS = jsonObject.getString("urlXs");

        this.taskL = startTask(storageL);
        this.taskM = startTask(storageM);
        this.taskS = startTask(storageS);
        this.taskXS = startTask(storageXS);
    }

    public enum Size {
        SIZE_XSMALL,
        SIZE_SMALL,
        SIZE_MEDIUM,
        SIZE_LARGE
    }

    public void fetchDownloadUrl(Size size, OnCompleteListener listener) {
        Task<Uri> task;

        switch (size){
            case SIZE_XSMALL:
                task = taskXS;

                break;

            case SIZE_SMALL:
                task = taskS;

                break;
            case SIZE_MEDIUM:
                task = taskM;

                break;
            case SIZE_LARGE:
                task = taskL;
                break;
            default:
                task = taskL;
        }

        super.fetchDownloadUrl(listener, task);

    }



    @Deprecated
    public void fetchDownloadUrl(OnCompleteListener listener) {
        fetchDownloadUrl(listener, taskL);
    }
}
