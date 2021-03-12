package com.elasticsearch.parent.business.demo.controller;

import cn.hutool.core.date.DateUtil;
import com.elasticsearch.parent.business.demo.entity.MDemo;
import com.elasticsearch.parent.business.demo.repository.MDemoRepository;
import com.elasticsearch.parent.utils.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 **/
@RequestMapping("/api")
@RestController
public class DemoController {

    @Autowired
    protected MDemoRepository repository;

    @GetMapping(value = "/query")
    public String query(MDemo param) {
        System.out.println(repository.findByIdIn(Collections.singletonList(param.getId())));

        page(param);
        sum(param);
        count(param);
        group(param);
        groupMore(param);
        sort(param);
        sortByDate(param);

        return "success";
    }

    /*
    分页
     */
    private Page<MDemo> page(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 拼装分页大小
                .withPageable(PageRequestUtil.of(0, 20))
                // 拼装排序条件
                .withSort(SortCompare.orderByDesc(MDemo::getCreateTime))
                .build();
        Page<MDemo> result = IndexPagingInfoBuilder.restPageInfo(repository.search(searchQuery), MDemo.class);
        return result;
    }


    /*
    单结果聚合
     */
    private void sum(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        LambdaAggregationBuilder aggregationBuilder = new LambdaAggregationBuilder();
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 不查询分页数据
                .withSourceFilter(LambdaFilterBuilder.emptyFilter())
                // 拼装聚合结果
                .addAggregation(aggregationBuilder.sum(MDemo::getPrice))
                .build();
        AggregatedPage<MDemo> search = (AggregatedPage<MDemo>) repository.search(searchQuery);

        System.out.println(LambdaAggregationResult.sumValue(search, aggregationBuilder.getAggName()));
    }

    /*
    多结果聚合
     */
    private void count(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        LambdaAggregationBuilder aggregationBuilder = new LambdaAggregationBuilder();
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 不查询分页数据
                .withSourceFilter(LambdaFilterBuilder.emptyFilter())
                // 拼装聚合结果
                .addAggregation(aggregationBuilder.sum(MDemo::getPrice))
                .addAggregation(aggregationBuilder.count(MDemo::getId))
                .addAggregation(aggregationBuilder.cardinality(MDemo::getCompanyId))
                .build();
        AggregatedPage<MDemo> search = (AggregatedPage<MDemo>) repository.search(searchQuery);

        System.out.println(LambdaAggregationResult.sumValue(search, aggregationBuilder.getAggName()));
        System.out.println(LambdaAggregationResult.countValue(search, aggregationBuilder.getAggName()));
        System.out.println(LambdaAggregationResult.cardinalityValue(search, aggregationBuilder.getAggName()));
    }

    /*
    多结果分组聚合
     */
    private void group(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        LambdaAggregationBuilder aggregationBuilder = new LambdaAggregationBuilder();
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 不查询分页数据
                .withSourceFilter(LambdaFilterBuilder.emptyFilter())
                // 拼装聚合结果
                .addAggregation(
                        aggregationBuilder.globalTerms(MDemo::getCompanyId, 10)
                                .resultSet(
                                        aggregationBuilder.sum(MDemo::getPrice, false),
                                        aggregationBuilder.count(MDemo::getId)
                                )
                                .getBuilder())
                .build();
        AggregatedPage<MDemo> search = (AggregatedPage<MDemo>) repository.search(searchQuery);

        List<MultiBucketsAggregation.Bucket> buckets = LambdaAggregationResult.getBuckets(search, aggregationBuilder.getAggName());

        String priceName = aggregationBuilder.getAggName();
        String countName = aggregationBuilder.getAggName();

        for (MultiBucketsAggregation.Bucket bucket : buckets) {
            System.out.println(Long.valueOf(bucket.getKeyAsString()));
            System.out.println(LambdaAggregationResult.sumValue(bucket, priceName));
            System.out.println(LambdaAggregationResult.countValue(bucket, countName));
        }
    }

    /*
    多结果两层分组聚合
     */
    private void groupMore(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        LambdaAggregationBuilder aggregationBuilder = new LambdaAggregationBuilder();
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 不查询分页数据
                .withSourceFilter(LambdaFilterBuilder.emptyFilter())
                // 拼装聚合结果
                .addAggregation(
                        aggregationBuilder.globalTerms(MDemo::getCompanyId, MDemo::getProvince)
                                .resultSet(
                                        aggregationBuilder.sum(MDemo::getPrice),
                                        aggregationBuilder.count(MDemo::getId)
                                )
                                .getBuilder())
                .build();
        AggregatedPage<MDemo> search = (AggregatedPage<MDemo>) repository.search(searchQuery);

        List<MultiBucketsAggregation.Bucket> buckets = LambdaAggregationResult.getBuckets(search, aggregationBuilder.getAggName());

        String provinceName = aggregationBuilder.getAggName();
        String priceName = aggregationBuilder.getAggName();
        String countName = aggregationBuilder.getAggName();

        for (MultiBucketsAggregation.Bucket bucket : buckets) {
            System.out.println(Long.valueOf(bucket.getKeyAsString()));

            List<MultiBucketsAggregation.Bucket> subBuckets = LambdaAggregationResult.getBuckets(bucket, provinceName);
            for (MultiBucketsAggregation.Bucket subBucket : subBuckets) {
                System.out.println(String.valueOf(subBucket.getKeyAsString()));
                System.out.println(LambdaAggregationResult.sumValue(subBucket, priceName));
                System.out.println(LambdaAggregationResult.countValue(subBucket, countName));
            }
        }
    }

    /*
    分组+排序聚合
     */
    private void sort(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        LambdaAggregationBuilder aggregationBuilder = new LambdaAggregationBuilder();
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 不查询分页数据
                .withSourceFilter(LambdaFilterBuilder.emptyFilter())
                // 拼装聚合结果
                .addAggregation(
                        aggregationBuilder.globalTerms(MDemo::getCompanyId)
                                .resultSet(
                                        aggregationBuilder.topHits(MDemo::getCreateTime)
                                )
                                .getBuilder())
                .build();
        AggregatedPage<MDemo> search = (AggregatedPage<MDemo>) repository.search(searchQuery);

        List<MultiBucketsAggregation.Bucket> buckets = LambdaAggregationResult.getBuckets(search, aggregationBuilder.getAggName());

        String dataName = aggregationBuilder.getAggName();

        for (MultiBucketsAggregation.Bucket bucket : buckets) {
            System.out.println(Long.valueOf(bucket.getKeyAsString()));

            final MDemo indexDeviceTankDataReport = LambdaAggregationResult.searchHitValue(bucket, dataName, MDemo.class);
            System.out.println(indexDeviceTankDataReport);
        }
    }

    /*
    按时间分组聚合
     */
    private void sortByDate(MDemo param) {
        LambdaQueryBuilder builder = getLambdaQueryBuilder(param);

        LambdaAggregationBuilder aggregationBuilder = new LambdaAggregationBuilder();
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 拼装查询条件
                .withQuery(builder.getQuery())
                // 不查询分页数据
                .withSourceFilter(LambdaFilterBuilder.emptyFilter())
                // 拼装聚合结果
                .addAggregation(
                        aggregationBuilder.globalDateHistogram(DateHistogramInterval.DAY, MDemo::getCreateTime)
                                .resultSet(
                                        aggregationBuilder.sum(MDemo::getPrice)
                                )
                                .getBuilder())
                .build();
        AggregatedPage<MDemo> search = (AggregatedPage<MDemo>) repository.search(searchQuery);

        List<MultiBucketsAggregation.Bucket> buckets = LambdaAggregationResult.getBuckets(search, aggregationBuilder.getAggName());

        String priceName = aggregationBuilder.getAggName();

        for (MultiBucketsAggregation.Bucket bucket : buckets) {
            System.out.println(DateUtil.parse(bucket.getKeyAsString()));
            System.out.println(LambdaAggregationResult.sumValue(bucket, priceName));
        }
    }

    private LambdaQueryBuilder getLambdaQueryBuilder(MDemo param) {
        LambdaQueryBuilder builder = LambdaQueryBuilder.query();
        builder.termQuery(null != param.getId(), MDemo::getId, param.getId())
                .commonTermsQuery(!StringUtils.isEmpty(param.getName()), MDemo::getName, param.getName())
                // .wildcardQuery(!StringUtils.isEmpty(param.getName()), MDemo::getName, param.getName())
                .ge(null != param.getCreateTime(), MDemo::getCreateTime, param.getCreateTime())
                .le(null != param.getCreateTime(), MDemo::getCreateTime, param.getCreateTime())
                .mustNot(LambdaQueryBuilder.query().termQuery(null != param.getCity(), MDemo::getCity, param.getCity()).getQuery())
        ;

        if (null != param.getProvince() || null != param.getCity()) {
            builder.must(LambdaQueryBuilder.query()
                    .should(LambdaQueryBuilder.query().wildcardQuery(null != param.getProvince(), MDemo::getProvince, param.getProvince()).getQuery())
                    .should(LambdaQueryBuilder.query().wildcardQuery(null != param.getCity(), MDemo::getCity, param.getCity()).getQuery())
                    .getQuery()
            );
        }
        return builder;
    }
}
