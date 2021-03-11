package com.elasticsearch.parent.business.demo.repository;


import com.elasticsearch.parent.business.demo.entity.MDemo;
import com.elasticsearch.parent.repository.IndexBaseRepository;
import org.springframework.stereotype.Component;

/**
 * <p>
 * </p>
 *
 * @author zj2626
 * @since 2020-03-18
 */
@Component
public interface MDemoRepository extends IndexBaseRepository<MDemo> {
}
