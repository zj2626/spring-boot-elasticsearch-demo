package com.elasticsearch.parent.utils;

import lombok.Builder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * 排序条件封装
 * <p>比较值</p>
 * https://www.cnblogs.com/-flq/p/9505888.html
 *
 * @since 2020-08-24
 */
@Builder
public class SortCompare extends SortBuilders {

    public static <T> FieldSortBuilder orderByDesc(SFunction<T> column) {
        return SortBuilders.fieldSort(BeanUtils.convertToFieldName(column)).order(SortOrder.DESC).unmappedType(FieldSortBuilder.NAME);
    }

    public static <T> FieldSortBuilder orderByAsc(SFunction<T> column) {
        return SortBuilders.fieldSort(BeanUtils.convertToFieldName(column)).order(SortOrder.ASC).unmappedType(FieldSortBuilder.NAME);
    }
}
