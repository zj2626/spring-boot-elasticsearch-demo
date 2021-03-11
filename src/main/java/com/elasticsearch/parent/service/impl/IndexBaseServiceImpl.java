package com.elasticsearch.parent.service.impl;

import com.elasticsearch.parent.entity.AbstractIndexBaseEntity;
import com.elasticsearch.parent.repository.IndexBaseRepository;
import com.elasticsearch.parent.service.IndexBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author zj2626
 * @description service层基类
 * @date 2020/4/21
 */
public abstract class IndexBaseServiceImpl<R extends IndexBaseRepository<T>, T extends AbstractIndexBaseEntity> implements IndexBaseService<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected R repository;

    public R getRepository() {
        return repository;
    }

    @Override
    public List<T> findByIdIn(Collection<? extends Serializable> idList) {
        return repository.findByIdIn(idList);
    }

    @Override
    public Iterable<T> saveAll(Collection<T> entities) {
        return repository.saveAll(entities);
    }
}
