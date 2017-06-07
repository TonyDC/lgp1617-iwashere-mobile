package com.teamc.mira.iwashere.domain.model.util;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.teamc.mira.iwashere.data.source.remote.impl.ResourcesRepositoryImpl;
import com.teamc.mira.iwashere.domain.repository.remote.ResourcesRepository;

import java.io.Serializable;

public abstract class Resource implements Serializable {
    protected Resource(Type type) {
        this.type = type;
    }

    public enum Type {
        IMAGE,
        VIDEO,
        AUDIO
    }

    protected final Type type;

    protected Task<Uri> startTask(String storageUrl){
        ResourcesRepository repository = new ResourcesRepositoryImpl();

        try{
            Task<Uri> task = repository.fetch(storageUrl);

            return task;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    protected void fetchDownloadUrl(OnCompleteListener listener, Task<Uri> task) {

        task.addOnCompleteListener(listener);
    }

    public Type getType() {
        return type;
    }

    public abstract void fetchDownloadUrl(OnCompleteListener listener);
}
