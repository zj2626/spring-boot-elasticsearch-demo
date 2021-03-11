package com.elasticsearch.parent.utils;

import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

/**
 * 过滤条件封装
 * <p>比较值</p>
 * https://www.cnblogs.com/-flq/p/9505888.html
 *
 * @since 2020-08-24
 */
public class LambdaFilterBuilder {
    public static FetchSourceFilter emptyFilter() {
        return new FetchSourceFilter(new String[]{""}, null);
    }

    public static <T> FetchSourceFilter filter(SFunction<T>... columns) {
        String[] fieldNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            fieldNames[i] = BeanUtils.convertToFieldName(columns[i]);
        }
        return new FetchSourceFilter(fieldNames, null);
    }
}
