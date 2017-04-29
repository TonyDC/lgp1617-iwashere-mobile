package com.teamc.mira.iwashere.domain.interactors.impl;

import android.content.Context;

import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.threading.TestMainThread;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by Duart on 20/04/2017.
 */

public class InteractorTest {
    MainThread mMainThread;
    @Mock
    Executor mExecutor;
    @Mock
    Context mContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mMainThread = new TestMainThread();
    }
}
