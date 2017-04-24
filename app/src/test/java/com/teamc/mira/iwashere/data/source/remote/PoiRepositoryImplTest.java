package com.teamc.mira.iwashere.data.source.remote;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.repository.PoiRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PoiRepositoryImplTest {
    @Mock Context mContext = mock(Context.class);
    @Mock RequestFuture<JSONArray> mFuture = mock(RequestFuture.class);
    @Mock MySingletonImpl mySingleton = mock(MySingletonImpl.class);
    @Mock RequestQueue requestQueue;

    final JSONArray successArray = new JSONArray("[\n" +
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

    public PoiRepositoryImplTest() throws JSONException {
    }

    @Before
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {

        when(mFuture.get(any(int.class), any(TimeUnit.class))).thenReturn(successArray);

    }

    @Test
    public void getPoiModelsFromRequest() throws RemoteDataException, InterruptedException, ExecutionException, TimeoutException, JSONException {

        PoiRepositoryImpl poiRepository = new PoiRepositoryImpl(requestQueue);

        ArrayList<PoiModel> poiModels = poiRepository.getPoiModelsFromRequest(mFuture);
        Mockito.verify(mFuture).get(any(int.class), any(TimeUnit.class));
        Mockito.verifyNoMoreInteractions(mFuture);
        assertTrue("museu soares dos reis".equals(poiModels.get(0).getName()));
        assertTrue("museu 2".equals(poiModels.get(1).getName()));
    }

}