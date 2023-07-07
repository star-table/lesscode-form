package com.polaris.lesscode.form.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author roamer
 * @version v1.0
 * @date 2021/3/8 16:54
 */
@Data
public class FieldAuthBo {

    @ApiModelProperty("字段名")
    private String name;

    @ApiModelProperty("可读")
    private Boolean readable;

    @ApiModelProperty("可编辑")
    private Boolean editable;

    @ApiModelProperty("脱敏的")
    private Boolean masking;

    public FieldAuthBo() {
        readable = Boolean.FALSE;
        editable = Boolean.FALSE;
        masking = Boolean.FALSE;
    }

    public Boolean getReadable() {
        return readable != null && readable;
    }

    public Boolean getEditable() {
        return editable != null && editable;
    }

    public Boolean getMasking() {
        return masking != null && masking;
    }
}
