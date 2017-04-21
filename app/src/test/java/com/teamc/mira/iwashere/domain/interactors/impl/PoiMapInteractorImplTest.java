package com.teamc.mira.iwashere.domain.interactors.impl;

import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.PoiRepositoryImpl;
import com.teamc.mira.iwashere.domain.interactors.PoiMapInteractor;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;
import com.teamc.mira.iwashere.domain.repository.Repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class PoiMapInteractorImplTest extends InteractorTest {

    @Mock PoiMapInteractor.CallBack mCallBack;
    @Mock PoiRepository mRepository;
    @Mock RequestFuture<JSONArray> future;


    JSONArray successArray = new JSONArray("[\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"name\": \"museu soares dos reis\",\n" +
                "    \"description\": \"Isto é uma descrição\",\n" +
                "    \"latitude\": 43.5,\n" +
                "    \"longitude\": 43.5,\n" +
                "    \"address\": \"Rua abc\",\n" +
                "    \"createdAt\": \"2017-04-20T14:13:38.957Z\",\n" +
                "    \"updatedAt\": \"2017-04-20T14:13:43.544Z\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 4,\n" +
                "    \"name\": \"museu 2\",\n" +
                "    \"description\": \"descrição 2\",\n" +
                "    \"latitude\": 43.7,\n" +
                "    \"longitude\": 43.7,\n" +
                "    \"address\": \"rua abc 2\",\n" +
                "    \"createdAt\": \"2017-04-20T14:22:59.517Z\",\n" +
                "    \"updatedAt\": \"2017-04-20T14:23:01.671Z\"\n" +
                "  }\n" +
                "]");

    public PoiMapInteractorImplTest() throws JSONException {
        super();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mRepository = new PoiRepositoryImpl(mContext);
        when(future.get()).thenReturn(successArray);
    }



    public void notifySuccess() throws Exception {

        PoiMapInteractorImpl poiMapInteractor = new PoiMapInteractorImpl(
                mExecutor,
                mMainThread,
                mCallBack,
                mRepository,
                4.3,4.4,4.3,4.4
                );


        Mockito.verify(mRepository).fetchPoisInArea(4.3,4.4,4.3,4.4);
        Mockito.verifyNoMoreInteractions(mRepository);
//        Mockito.verify(mCallBack).onSuccess();
    }

    @Test
    public void notifyError() throws Exception {

    }

}