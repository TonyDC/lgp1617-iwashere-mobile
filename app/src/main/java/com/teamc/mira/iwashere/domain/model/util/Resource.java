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

    public Resource(String storageUrl){
        this.storageUrl = storageUrl;
//        fetchDownloadUrl();
//        this.downloadUrl = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl).getDownloadUrl().getResult().toString();
    }


    public Resource(String storageUrl, String downloadUrl){
        this.storageUrl = storageUrl;
    }

    public void fetchDownloadUrl(){
        OnCompleteListener listener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                downloadUrl = task.getResult().toString();
            }
        };

        fetchDownloadUrl(listener);

    }

    public void fetchDownloadUrl(OnCompleteListener listener) {
        try{
            Task<Uri> task = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl).getDownloadUrl();
            task.addOnCompleteListener(listener);
        }catch (Exception e){
            e.printStackTrace();
        }
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

