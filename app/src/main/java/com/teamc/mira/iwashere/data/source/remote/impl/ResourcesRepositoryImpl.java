package com.teamc.mira.iwashere.data.source.remote.impl;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.teamc.mira.iwashere.domain.repository.remote.ResourcesRepository;

public class ResourcesRepositoryImpl implements ResourcesRepository {
    String storageBucket = "gs://iwashere-mobile.appspot.com";
    String storageUrl = storageBucket + "/";


    @Override
    public Task<Uri> fetch(String storageId) {
        String url = storageUrl + storageId;
        return FirebaseStorage.getInstance().getReferenceFromUrl(url).getDownloadUrl();
    }
}
