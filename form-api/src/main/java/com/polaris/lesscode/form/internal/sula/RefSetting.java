package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import lombok.Data;

@Data
public class RefSetting {
    private Long tableId;
    private String columnId;
    private Condition condition;
    private String aggFunc;
    private Integer numberType;
}
