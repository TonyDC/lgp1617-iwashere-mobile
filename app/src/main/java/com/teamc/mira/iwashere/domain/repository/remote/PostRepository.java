package com.teamc.mira.iwashere.domain.repository.remote;

/**
 * Created by Duart on 01/05/2017.
 */

public interface PostRepository {

    boolean like(String userId, String postId, boolean liked);

    boolean fetch(String userId, String poiId, int offset, int limit);

    boolean fetch(String poiId, int offset, int limit);
}
