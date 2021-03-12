package com.elasticsearch.parent.business.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * </p>
 *
 * @author zj2626
 * @since 2020-03-18
 */
@Data
@Document(indexName = MDemo.INDEX_NAME)
public class MDemo implements Serializable {
    public static final String INDEX_NAME = "project_name." + "k_demo";

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) // 主键生成策略
    private Long id;

    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword)
    private String province;
    @Field(type = FieldType.Keyword)
    private String city;
    @Field(type = FieldType.Keyword)
    private String area;
    @Field(type = FieldType.Keyword)
    private String address;
    @Field(type = FieldType.Long)
    private Long companyId;
    @Field(type = FieldType.Double)
    private BigDecimal price;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新人ID
     */
    private Long updateUserId;

    // 逻辑删除 删除状态 0已删除 1未删除
    private Boolean deleteStatus;
}
