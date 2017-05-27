package com.teamc.mira.iwashere.domain.model.util;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class BasicResource extends Resource {

    private String storageUrl;
    private String downloadUrl;

    public BasicResource(Type type, String storageUrl){
        super(type);
        this.storageUrl = storageUrl;
    }


    public BasicResource(Type type, String storageUrl, String downloadUrl){
        super(type);
        this.storageUrl = storageUrl;
    }

    private void fetchDownloadUrl(){
        OnCompleteListener listener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                downloadUrl = task.getResult().toString();
            }
        };

        fetchDownloadUrl(listener);

    }

    @Override
    public void fetchDownloadUrl(OnCompleteListener listener) {
        fetchDownloadUrl(listener, storageUrl);
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getDownloadUrl(){
        return downloadUrl;
    }
}

