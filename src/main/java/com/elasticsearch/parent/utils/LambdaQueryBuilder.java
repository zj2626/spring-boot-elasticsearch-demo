package com.elasticsearch.parent.utils;

import cn.hutool.core.date.DateUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;

/**
 * 查询条件封装
 * <p>比较值</p>
 * https://www.cnblogs.com/-flq/p/9505888.html
 *
 * @since 2020-08-24
 */
public class LambdaQueryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(LambdaQueryBuilder.class);

    protected BoolQueryBuilder query;

    public static LambdaQueryBuilder query() {
        return new LambdaQueryBuilder(new BoolQueryBuilder());
    }

    private LambdaQueryBuilder() {
    }

    private LambdaQueryBuilder(BoolQueryBuilder query) {
        this.query = query;
    }

    public BoolQueryBuilder getQuery() {
        return query;
    }

    /**
     * must 对应mybatisplus中的: .and()
     */
    public LambdaQueryBuilder must(BoolQueryBuilder queryBuilder) {
        if (hasClauses(queryBuilder)) {
            getQuery().must(queryBuilder);
        }
        return this;
    }

    /**
     * mustNot 对应mybatisplus中的: .not()
     */
    public LambdaQueryBuilder mustNot(BoolQueryBuilder queryBuilder) {
        if (hasClauses(queryBuilder)) {
            getQuery().mustNot(queryBuilder);
        }
        return this;
    }

    /**
     * should 对应mybatisplus中的: .or()
     */
    public LambdaQueryBuilder should(BoolQueryBuilder queryBuilder) {
        if (hasClauses(queryBuilder)) {
            getQuery().should(queryBuilder);
        }
        return this;
    }

    /**
     * filter
     */
    public LambdaQueryBuilder filter(BoolQueryBuilder queryBuilder) {
        if (hasClauses(queryBuilder)) {
            getQuery().filter(queryBuilder);
        }
        return this;
    }


    /**
     * 等于 = 应用于类型为 字符串 的字段 (查询的字段类型为text会分词)
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder commonTermsQuery(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.commonTermsQuery(BeanUtils.convertToFieldName(column), assembleParam(val)));
        }
        return this;
    }

    /**
     * 等于 = 应用于类型为 数值(Long, Double, Integer, BigDecimal) 的字段
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder termQuery(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.termQuery(BeanUtils.convertToFieldName(column), assembleParam(val)));
        }
        return this;
    }

    /**
     * 大于 &gt;
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder gt(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.rangeQuery(BeanUtils.convertToFieldName(column)).gt(assembleParam(val)));
        }
        return this;
    }

    /**
     * 大于等于 &gt;=
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder ge(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.rangeQuery(BeanUtils.convertToFieldName(column)).gte(assembleParam(val)));
        }
        return this;
    }

    /**
     * 小于 &lt;
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder lt(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.rangeQuery(BeanUtils.convertToFieldName(column)).lt(assembleParam(val)));
        }
        return this;
    }

    /**
     * 小于等于 &lt;=
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder le(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.rangeQuery(BeanUtils.convertToFieldName(column)).lte(assembleParam(val)));
        }
        return this;
    }

    /**
     * BETWEEN 值1 AND 值2
     *
     * @param column 字段
     * @param val1   值1
     * @param val2   值2
     * @return children
     */
    public <T> LambdaQueryBuilder rangeQuery(boolean condition, SFunction<T> column, Object val1, Object val2) {
        if (condition) {
            getQuery().must(QueryBuilders.rangeQuery(BeanUtils.convertToFieldName(column)).from(assembleParam(val1)).to(assembleParam(val2)).includeLower(true).includeUpper(true));
        }
        return this;
    }

    /**
     * LIKE '%值%'
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder wildcardQuery(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery(BeanUtils.convertToFieldName(column), "*" + assembleParam(val) + "*")));
        }
        return this;
    }

    /**
     * 一个字段匹配多个值
     * IN
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder termsQuery(boolean condition, SFunction<T> column, Object... val) {
        if (condition) {
            getQuery().must(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(BeanUtils.convertToFieldName(column), val)));
        }
        return this;
    }

    /**
     * 一个字段匹配多个值
     * IN
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder termsQuery(boolean condition, SFunction<T> column, Collection<?> val) {
        if (condition) {
            getQuery().must(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(BeanUtils.convertToFieldName(column), val)));
        }
        return this;
    }

    /**
     * 多个字段匹配某一个值
     *
     * @param columns 字段
     * @param val     值
     * @return children
     */
    public <T> LambdaQueryBuilder multiMatchQuery(boolean condition, Object val, SFunction<T>... columns) {
        if (condition) {
            String[] fieldNames = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                fieldNames[i] = BeanUtils.convertToFieldName(columns[i]);
            }
            getQuery().must(QueryBuilders.boolQuery().must(QueryBuilders.multiMatchQuery(assembleParam(val), fieldNames)));
        }
        return this;
    }

    /**
     * LIKE '值%'
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    public <T> LambdaQueryBuilder matchQuery(boolean condition, SFunction<T> column, Object val) {
        if (condition) {
            getQuery().must(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(BeanUtils.convertToFieldName(column), assembleParam(val))));
        }
        return this;
    }

    /**
     * 查询条件特殊处理
     * 1. 时间类型-转换为时间戳
     *
     * @param param
     * @return java.lang.Object
     * @author zj2626
     * @date 2020/8/31
     */
    private Object assembleParam(Object param) {
        if (param instanceof Date) {
            // return ((Date) param).getTime();
            return DateUtil.offsetHour(((Date) param), 0).getTime();
        }
        return param;
    }

    private boolean hasClauses(BoolQueryBuilder queryBuilder) {
        return !CollectionUtils.isEmpty(queryBuilder.must())
                || !CollectionUtils.isEmpty(queryBuilder.mustNot())
                || !CollectionUtils.isEmpty(queryBuilder.filter())
                || !CollectionUtils.isEmpty(queryBuilder.should());
    }
}
