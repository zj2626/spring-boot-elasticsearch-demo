package com.elasticsearch.parent.utils;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * 查询分组聚合封装
 * <p>比较值</p>
 *
 * @since 2020-08-24
 */
public class LambdaAggregationBuilder {
    protected static final Logger logger = LoggerFactory.getLogger(LambdaAggregationBuilder.class);

    public static final String FORMAT_DAY = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_MONTH = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_STRING = "%s_%s_%s";

    protected Integer aggregationNamesSize = 0;
    protected List<String> aggregationNames = new ArrayList<>();
    protected static final String TERMS = "TERMS";

    protected static final String COUNT = "COUNT";
    protected static final String CARDINALITY = "CARDINALITY";
    protected static final String SUM = "SUM";
    protected static final String AVG = "AVG";
    protected static final String MAX = "MAX";
    protected static final String MIN = "MIN";
    protected static final String SCRIPT = "SCRIPT";
    protected static final String TOP_HITS = "TOP_HITS";

    protected AbstractAggregationBuilder<?> aggregationBuilder;
    protected AbstractAggregationBuilder<?> subAggregationBuilder;

    protected void setBuilder(AbstractAggregationBuilder<?> aggregationBuilder) {
        this.aggregationBuilder = aggregationBuilder;
        this.subAggregationBuilder = aggregationBuilder;
    }

    protected void setSubBuilder(AbstractAggregationBuilder<?> aggregationBuilder) {
        this.subAggregationBuilder = aggregationBuilder;
    }

    public AbstractAggregationBuilder<?> getBuilder() {
        return aggregationBuilder;
    }

    public AbstractAggregationBuilder<?> getSubBuilder() {
        return subAggregationBuilder;
    }

    /*
     ********************************************************************************************************************
     *                                                                                                                  *
     *                                                聚合条件                                                            *
     *                                                                                                                  *
     ********************************************************************************************************************
     */

    /*
        使用给定字段进行分组, 可多个参数,按照从前到后的顺序进行先后分组。

        注意: 默认返回分类个数为10条 (目前设置为 16384)
     */
    @SuppressWarnings(value = "unchecked")
    public <T> LambdaAggregationBuilder globalTerms(SFunction<T>... columns) {
        for (SFunction<T> column : columns) {
            globalTerms(column, 0);
        }
        return this;
    }

    @SuppressWarnings(value = "unchecked")
    public <T> LambdaAggregationBuilder globalTerms(SFunction<T> column, Integer size) {
        final LambdaBo bo = getColumn(column, TERMS);
        aggregationNames.add(bo.getFormatName());
        final TermsAggregationBuilder builder = AggregationBuilders
                .terms(bo.getFormatName())
                .field(bo.getColumnName())
                .size(null == size || size <= 0 ? 16384 : size);
        if (null == getBuilder()) {
            setBuilder(builder);
        } else if (null == getSubBuilder()) {
            getBuilder().subAggregation(builder);
            setSubBuilder(builder);
        } else {
            getSubBuilder().subAggregation(builder);
            setSubBuilder(builder);
        }
        aggregationNamesSize++;
        return this;
    }

    /*
        使用给定时间类型的字段进行分组
     */
    @SuppressWarnings(value = "unchecked")
    public <T> LambdaAggregationBuilder globalDateHistogram(DateHistogramInterval histogramInterval, SFunction<T> column) {
        final LambdaBo bo = getColumn(column, TERMS);
        aggregationNames.add(bo.getFormatName());
        final DateHistogramAggregationBuilder builder = AggregationBuilders
                .dateHistogram(bo.getFormatName())
                .field(bo.getColumnName())
                .dateHistogramInterval(histogramInterval)
                .format(FORMAT_DAY)
                // 时区区间
                .timeZone(DateTimeZone.forID(TimeZone.getDefault().getID()))
                //                .timeZone(DateTimeZone.forOffsetHours(0))
                ;
        if (null == getBuilder()) {
            setBuilder(builder);
        } else {
            getBuilder().subAggregation(builder);
            setSubBuilder(builder);
        }
        aggregationNamesSize++;
        return this;
    }

    /*
    分组结果集拼接到 聚合 上
     */
    public LambdaAggregationBuilder resultSet(AbstractAggregationBuilder<?>... builders) {
        if (null == getSubBuilder()) {
            throw new RuntimeException("聚合查询未初始化");
        }
        if (null != builders && builders.length > 0) {
            for (AbstractAggregationBuilder<?> builder : builders) {
                getSubBuilder().subAggregation(builder);
            }
        }
        return this;
    }

    /*
     ********************************************************************************************************************
     *                                                                                                                  *
     *                                                聚合操作                                                            *
     *                                                                                                                  *
     ********************************************************************************************************************
     */

    /**
     * 统计个数
     */
    public <T> AbstractAggregationBuilder<?> count(SFunction<T> column) {
        return count(column, null);
    }

    public <T> AbstractAggregationBuilder<?> count(SFunction<T> column, Boolean asc) {
        final LambdaBo bo = getColumn(column, COUNT);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.count(bo.getFormatName()).field(bo.getColumnName());
        aggregationNamesSize++;
        order(bo, asc);
        return result;
    }

    /**
     * 去重+统计个数
     */
    public <T> AbstractAggregationBuilder<?> cardinality(SFunction<T> column) {
        return cardinality(column, null);
    }

    public <T> AbstractAggregationBuilder<?> cardinality(SFunction<T> column, Boolean asc) {
        final LambdaBo bo = getColumn(column, CARDINALITY);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.cardinality(bo.getFormatName()).field(bo.getColumnName());
        aggregationNamesSize++;
        order(bo, asc);
        return result;
    }

    /**
     * 求和  (默认不排序)
     */
    public <T, R> AbstractAggregationBuilder<?> sum(SFunction<T> column) {
        return sum(column, null);
    }

    public <T, R> AbstractAggregationBuilder<?> sum(SFunction<T> column, Boolean asc) {
        final LambdaBo bo = getColumn(column, SUM);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.sum(bo.getFormatName()).field(bo.getColumnName());
        aggregationNamesSize++;
        order(bo, asc);
        return result;
    }

    /**
     * 求平均 (默认不排序)
     */
    public <T> AbstractAggregationBuilder<?> avg(SFunction<T> column) {
        return avg(column, null);
    }

    public <T> AbstractAggregationBuilder<?> avg(SFunction<T> column, Boolean asc) {
        final LambdaBo bo = getColumn(column, AVG);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.avg(bo.getFormatName()).field(bo.getColumnName());
        aggregationNamesSize++;
        order(bo, asc);
        return result;
    }

    /**
     * 求最大 (默认不排序)
     */
    public <T> AbstractAggregationBuilder<?> max(SFunction<T> column) {
        return max(column, null);
    }

    public <T> AbstractAggregationBuilder<?> max(SFunction<T> column, Boolean asc) {
        final LambdaBo bo = getColumn(column, MAX);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.max(bo.getFormatName()).field(bo.getColumnName());
        aggregationNamesSize++;
        order(bo, asc);
        return result;
    }

    /**
     * 求最小 (默认不排序)
     */
    public <T> AbstractAggregationBuilder<?> min(SFunction<T> column) {
        return min(column, null);
    }

    public <T> AbstractAggregationBuilder<?> min(SFunction<T> column, Boolean asc) {
        final LambdaBo bo = getColumn(column, MIN);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.min(bo.getFormatName()).field(bo.getColumnName());
        aggregationNamesSize++;
        order(bo, asc);
        return result;
    }

    // 版本bug导致不可用 需升级
    public <T> AbstractAggregationBuilder<?> scriptDemo(SFunction<T> column) {
        final LambdaBo bo = getColumn(column, SCRIPT);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result = AggregationBuilders.scriptedMetric(bo.getFormatName())
                .initScript(new Script("state.transactions = []"))
                .mapScript(new Script("state.transactions.add(doc.orderQuantity.value * doc.price.value)"))
                .combineScript(new Script("double profit = 0; for (t in state.transactions) { profit += t } return profit"))
                .reduceScript(new Script("double profit = 0; for (a in states) { profit += a } return profit"));
        aggregationNamesSize++;
        // order(bo, asc);
        return result;
    }

    /**
     * 根据排序规则获取逆序结果的第一个 (不支持.order()方法的排序)
     *
     * @param sortColumn
     */
    public <T> AbstractAggregationBuilder<?> topHits(SFunction<T> sortColumn) {
        return topHits(sortColumn, SortOrder.DESC, 1);
    }

    /**
     * 根据排序规则获取排序结果的前size个, (这个是取当前聚合结果中, 符合排序的前n个数据 ---> 先排序,后取值)
     *
     * @param sortColumn
     * @param sortColumn 排序自动
     * @param sortOrder  排序顺序 asc/desc
     * @param size       需要的结果集个数
     * @return org.elasticsearch.search.aggregations.AbstractAggregationBuilder<?>
     * @author zj2626
     * @date 2020/9/21
     */
    public <T> AbstractAggregationBuilder<?> topHits(SFunction<T> sortColumn, SortOrder sortOrder, int size) {
        final LambdaBo bo = getColumn(sortColumn, TOP_HITS);
        aggregationNames.add(bo.getFormatName());
        AbstractAggregationBuilder<?> result =
                AggregationBuilders.topHits(bo.getFormatName()).sort(BeanUtils.convertToFieldName(sortColumn), null == sortOrder ? SortOrder.DESC : sortOrder).size(0 == size ? 1 : size);
        aggregationNamesSize++;
        return result;
    }

    /*
        对分组结果进行排序; 是否正序排序(null则表示不排序) (和topHits功能不同, 这个是把聚合的结果按照当前的聚合key进行排序 ---> 先取值,后排序)
     */
    protected void order(LambdaBo bo, Boolean asc) {
        if (null != asc) {
            final AbstractAggregationBuilder<?> builder = getBuilder();
            if (builder instanceof TermsAggregationBuilder) {
                ((TermsAggregationBuilder) builder).order(BucketOrder.aggregation(bo.getFormatName(), asc));
            }
        }
    }

    protected <T> LambdaBo getColumn(SFunction<T> column, String operateType) {
        StringBuilder columnName = new StringBuilder(BeanUtils.convertToFieldName(column));
        //        if (null != subColumns && subColumns.length > 0) {
        //            for (SFunction<R> subColumn : subColumns) {
        //                columnName.append(".").append(BeanUtils.convertToFieldName(subColumn));
        //            }
        //        }
        final String formatName = String.format(FORMAT_STRING, operateType, aggregationNamesSize, columnName.toString());
        return LambdaBo.builder().columnName(columnName.toString()).formatName(formatName).build();
    }

    public String getAggName() {
        try {
            return aggregationNames.remove(0);
        } catch (Exception e) {
            logger.error("聚合查询的结果集不存在", e);
            throw new RuntimeException("聚合查询的结果集不存在");
        }
    }
}
