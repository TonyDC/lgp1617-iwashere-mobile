package com.teamc.mira.iwashere.domain.repository.remote;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface ResourcesRepository {
    Task<Uri> fetch(String storageId);
}
