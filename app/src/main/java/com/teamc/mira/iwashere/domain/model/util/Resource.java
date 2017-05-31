package com.teamc.mira.iwashere.domain.model.util;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.teamc.mira.iwashere.data.source.remote.impl.ResourcesRepositoryImpl;
import com.teamc.mira.iwashere.domain.repository.remote.ResourcesRepository;

public abstract class Resource {
    protected Resource(Type type) {
        this.type = type;
    }

    public enum Type {
        IMAGE,
        VIDEO,
        AUDIO
    }

    protected final Type type;


    protected void fetchDownloadUrl(OnCompleteListener listener, String storageUrl) {
        ResourcesRepository repository = new ResourcesRepositoryImpl();

        try{
            Task<Uri> task = repository.fetch(storageUrl);
            task.addOnCompleteListener(listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Type getType() {
        return type;
    }

    public abstract void fetchDownloadUrl(OnCompleteListener listener);
}
