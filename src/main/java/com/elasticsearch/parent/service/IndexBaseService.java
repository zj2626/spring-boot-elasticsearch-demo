package com.elasticsearch.parent.service;


import com.elasticsearch.parent.entity.AbstractIndexBaseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IndexBaseService<T extends AbstractIndexBaseEntity> {

//    List<T> findByIdIn(Collection<? extends Serializable> idList);

    Iterable<T> saveAll(Collection<T> entities);
}
