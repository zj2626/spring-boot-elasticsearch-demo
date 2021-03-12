package com.elasticsearch.parent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class IndexBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) // 主键生成策略
    private Long id;

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
