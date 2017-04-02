package com.teamc.mira.iwashere.threading;

import android.os.Handler;
import android.os.Looper;

import com.teamc.mira.iwashere.domain.executor.MainThread;


/**
 * Created by Duart on 02/04/2017.
 */

public class MainThreadImpl implements MainThread {
    private static MainThread sMainThread;

    private Handler mHandler;

    private MainThreadImpl() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static MainThread getInstance() {
        if (sMainThread == null) {
            sMainThread = new MainThreadImpl();
        }

        return sMainThread;
    }
}
