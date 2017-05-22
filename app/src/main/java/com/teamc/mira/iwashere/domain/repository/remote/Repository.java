package com.teamc.mira.iwashere.domain.repository.remote;


import com.teamc.mira.iwashere.domain.model.BasicModel;

/**
 * A sample repository with CRUD operations on a model.
 */
public interface Repository {

    boolean insert(BasicModel model);

    boolean update(BasicModel model);

    BasicModel get(Object id);

    boolean delete(BasicModel model);
}
