package com.elasticsearch.parent.business.demo.service.impl;

import com.elasticsearch.parent.business.demo.entity.MDemo;
import com.elasticsearch.parent.business.demo.repository.MDemoRepository;
import com.elasticsearch.parent.service.impl.IndexBaseServiceImpl;
import com.elasticsearch.parent.business.demo.service.MDemoService;
import org.springframework.stereotype.Service;

/**
 * @name MDemoImpl
 * @description
 * @create 2021-03-11 14:01
 **/
@Service
public class MDemoServiceImpl extends IndexBaseServiceImpl<MDemoRepository, MDemo> implements MDemoService {
}
