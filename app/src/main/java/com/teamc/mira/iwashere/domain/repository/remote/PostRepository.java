package com.teamc.mira.iwashere.domain.repository.remote;

import com.teamc.mira.iwashere.data.source.remote.exceptions.RemoteDataException;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.PostModel;
import com.teamc.mira.iwashere.domain.model.util.Resource;


import org.json.JSONObject;

import java.io.File;
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

    ArrayList<PostModel> fetchPOIPosts(PoiModel poi, int contentOffset, int contentLimit) throws RemoteDataException;

    boolean addPostLike(PostModel post, String userId) throws RemoteDataException;

    boolean getPostLike(PostModel post) throws RemoteDataException;

    boolean post(String poiId, String description, ArrayList<String> tags, File resource);

    boolean updatePostLike(PostModel post, String userId, boolean liked) throws RemoteDataException;
}
