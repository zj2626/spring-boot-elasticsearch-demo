package com.elasticsearch.parent.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zj2626
 * @name LambdaVo
 * @description
 * @create 2020-10-19 16:36
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaBo {
    private String columnName;
    private String formatName;
}
