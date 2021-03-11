package com.elasticsearch.parent.business.demo.entity;

import com.elasticsearch.parent.entity.IndexBaseEntity;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * <p>
 * </p>
 *
 * @author zj2626
 * @since 2020-03-18
 */
@Data
@Document(indexName = MDemo.INDEX_NAME)
public class MDemo extends IndexBaseEntity {
    public static final String INDEX_NAME = "project_name." + "k_demo";

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


}
