package com.elasticsearch.parent.repository;

import com.elasticsearch.parent.entity.AbstractIndexBaseEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IndexBaseRepository<T extends AbstractIndexBaseEntity> extends ElasticsearchRepository<T, Long> {
    List<T> findByIdIn(Collection<? extends Serializable> ids);
}
