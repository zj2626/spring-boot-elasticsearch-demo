package com.elasticsearch.parent.business.demo.repository;


import com.elasticsearch.parent.business.demo.entity.MDemo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author zj2626
 * @since 2020-03-18
 */
@Component
public interface MDemoRepository extends ElasticsearchRepository<MDemo, Long> {
    List<MDemo> findByIdIn(Collection<? extends Serializable> ids);
}
