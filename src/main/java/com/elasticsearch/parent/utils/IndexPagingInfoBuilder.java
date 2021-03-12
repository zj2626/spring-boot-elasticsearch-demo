package com.elasticsearch.parent.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.List;

public class IndexPagingInfoBuilder {


    public static <T> Page<T> restPageInfo(Page pageInfo, List<T> content) {
        final Page<T> page = new AggregatedPageImpl<>(content, PageRequestUtil.of(pageInfo.getNumber() + 1, pageInfo.getSize()), pageInfo.getTotalElements());
        return page;
    }

    /**
     * 分页列表数据类型直接强制转换
     *
     * @param pageInfo
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> Page<T> restPageInfo(Page pageInfo, Class<T> targetClass) {
        final List<T> content = TransformUtils.listCopy(pageInfo.getContent(), targetClass);
        final Page<T> page = new AggregatedPageImpl<>(content, PageRequestUtil.of(pageInfo.getNumber() + 1, pageInfo.getSize()), pageInfo.getTotalElements());
        return page;
    }
}
