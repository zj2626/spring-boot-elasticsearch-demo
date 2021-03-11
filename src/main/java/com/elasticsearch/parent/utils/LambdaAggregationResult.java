package com.elasticsearch.parent.utils;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.InternalMultiBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 查询分组结果获取封装
 * <p>比较值</p>
 *
 * @since 2020-08-24
 */
public class LambdaAggregationResult {

    /*
        getBuckets
     */
    public static List<MultiBucketsAggregation.Bucket> getBuckets(AggregatedPage search, String aggName) {
        return ((InternalMultiBucketAggregation) search.getAggregation(aggName)).getBuckets();
    }

    public static List<MultiBucketsAggregation.Bucket> getBuckets(MultiBucketsAggregation.Bucket bucket, String aggName) {
        return ((InternalMultiBucketAggregation) bucket.getAggregations().asMap().get(aggName)).getBuckets();
    }

    /*
        获取求和结果
     */
    public static BigDecimal sumValue(AggregatedPage search, String aggName) {
        return BigDecimal.valueOf(((InternalSum) search.getAggregation(aggName)).getValue());
    }

    public static BigDecimal sumValue(MultiBucketsAggregation.Bucket bucket, String aggName) {
        return BigDecimal.valueOf(((InternalSum) bucket.getAggregations().asMap().get(aggName)).getValue());
    }

    /*
        获取求和结果
     */
    public static Long cardinalityValue(AggregatedPage search, String aggName) {
        return ((InternalCardinality) search.getAggregation(aggName)).getValue();
    }

    public static Long cardinalityValue(MultiBucketsAggregation.Bucket bucket, String aggName) {
        return ((InternalCardinality) bucket.getAggregations().asMap().get(aggName)).getValue();
    }


    /*
        获取个数结果
     */
    public static Long countValue(AggregatedPage search, String aggName) {
        return ((InternalValueCount) search.getAggregation(aggName)).getValue();
    }

    public static Long countValue(MultiBucketsAggregation.Bucket bucket, String aggName) {
        return ((InternalValueCount) bucket.getAggregations().asMap().get(aggName)).getValue();
    }

    /*
        获取topHits结果, 并转换为其他类型list, 取第一个
     */
    public static <T> T searchHitValue(AggregatedPage search, String aggName, Class<T> classType) {
        List<T> list = getHitList(classType, searchHitFullValue(search, aggName));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static <T> T searchHitValue(MultiBucketsAggregation.Bucket bucket, String aggName, Class<T> classType) {
        List<T> list = getHitList(classType, searchHitFullValue(bucket, aggName));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /*
        获取topHits结果, 并转换为其他类型list
     */
    public static <T> List<T> searchHitFullValue(AggregatedPage search, String aggName, Class<T> classType) {
        return getHitList(classType, searchHitFullValue(search, aggName));
    }

    public static <T> List<T> searchHitFullValue(MultiBucketsAggregation.Bucket bucket, String aggName, Class<T> classType) {
        return getHitList(classType, searchHitFullValue(bucket, aggName));
    }

    /*
        获取topHits结果
     */
    public static SearchHit[] searchHitFullValue(AggregatedPage search, String aggName) {
        return ((InternalTopHits) search.getAggregation(aggName)).getHits().getHits();
    }

    public static SearchHit[] searchHitFullValue(MultiBucketsAggregation.Bucket bucket, String aggName) {
        return ((InternalTopHits) bucket.getAggregations().asMap().get(aggName)).getHits().getHits();
    }

    private static <T> List<T> getHitList(Class<T> classType, SearchHit[] searchHits) {
        if (null == searchHits || 0 == searchHits.length) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(searchHits.length);
        for (SearchHit hit : searchHits) {
            final T parseObject = JSON.parseObject(JSON.toJSONString(hit.getSourceAsMap()), classType);
            if (null != parseObject) {
                result.add(parseObject);
            }
        }
        return result;
    }
}
