package com.teamc.mira.iwashere.domain.repository.remote;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;


import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Duart on 01/05/2017.
 */

public interface PostRepository {

    /**
     *
     * @param postId the POST's id
     * @return PostModel with information about the Post
     * @throws RemoteDataException
     * @see PostModel( JSONObject )
     */
    PostModel fetchPost(String postId) throws RemoteDataException;


    boolean post(String poiId, String description, ArrayList<String> tags, Resource resource);
}
