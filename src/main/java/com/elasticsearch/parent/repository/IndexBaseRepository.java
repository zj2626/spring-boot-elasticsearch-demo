package com.elasticsearch.parent.repository;

import com.elasticsearch.parent.entity.IndexBaseEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IndexBaseRepository<T extends IndexBaseEntity> extends ElasticsearchRepository<T, Long> {
    List<T> findByIdIn(Collection<? extends Serializable> ids);
}
