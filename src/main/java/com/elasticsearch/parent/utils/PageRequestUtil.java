package com.elasticsearch.parent.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author zj2626
 * @name PageRequestUtil
 * @description
 * @create 2020-08-25 14:14
 **/
public class PageRequestUtil extends PageRequest {

    protected PageRequestUtil(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static PageRequestUtil of(int page, int size) {
        return new PageRequestUtil(page > 0 ? page - 1 : 0, size, Sort.unsorted());
    }

    public static PageRequestUtil of(int page, int size, Sort sort) {
        return new PageRequestUtil(page > 0 ? page - 1 : 0, size, sort);
    }

    public static PageRequestUtil of(int page, int size, Sort.Direction direction, String... properties) {
        return new PageRequestUtil(page > 0 ? page - 1 : 0, size, Sort.by(direction, properties));
    }
}
