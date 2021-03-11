package com.elasticsearch.parent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class IndexBaseEntity extends AbstractIndexBaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) // 主键生成策略
    private Long id;
}
