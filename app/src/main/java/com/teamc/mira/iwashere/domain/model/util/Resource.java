package com.teamc.mira.iwashere.domain.model.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;

public class Resource {

    public String storageUrl;
    public String downloadUrl;

    Resource(String storageUrl){
        this.storageUrl = storageUrl;
        Task<Uri> task = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl).getDownloadUrl();
        task.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                downloadUrl = task.getResult().toString();
            }
        });
    }

    Resource(String storageUrl, String downloadUrl){
        this.storageUrl = storageUrl;
        this.downloadUrl = downloadUrl;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getDownloadUrl(){
        if(downloadUrl == null){
            Task<Uri> task = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl).getDownloadUrl();
            downloadUrl = task.getResult().toString();
        }

        return downloadUrl;
    }
}

