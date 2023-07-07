package com.polaris.lesscode.form.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * match
 *
 * @author ethanliao
 * @date 2020/12/30
 * @since 1.0.0
 */
@Data
@ApiModel("匹配")
public class Match {
    private String field;
    private String text;
    private String type;

}
