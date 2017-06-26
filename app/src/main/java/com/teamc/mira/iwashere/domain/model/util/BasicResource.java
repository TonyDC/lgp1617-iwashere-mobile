package com.teamc.mira.iwashere.domain.model.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class BasicResource extends Resource {

    private String storageUrl;
    private String downloadUrl;

    private Task<Uri> task;

    public BasicResource(Type type, String storageUrl){
        super(type);
        this.storageUrl = storageUrl;

        task = startTask(storageUrl);
    }


    public BasicResource(Type type, String storageUrl, String downloadUrl){
        super(type);
        task = startTask(storageUrl);
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
        fetchDownloadUrl(listener, task);
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

