package com.elasticsearch.parent.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class AbstractIndexBaseEntity implements Serializable {


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间 排序可用
     */
    @Field(type = FieldType.Date)
    private Date createTimeForSort;

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
    private Date updateTime;

    /**
     * 更新时间 排序可用
     */
    @Field(type = FieldType.Date)
    private Date updateTimeForSort;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新人ID
     */
    private Long updateUserId;

    @Field(type = FieldType.Date)
    private Date syncData;

    // 逻辑删除 删除状态 0已删除 1未删除
    private Boolean deleteStatus;

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
        this.createTimeForSort = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        this.updateTimeForSort = updateTime;
    }
}
