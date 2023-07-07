package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.form.internal.sula.ActionParam;
import com.polaris.lesscode.form.internal.sula.Constraint;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.sula.RemoteSourceParam;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author Liu.B.J
 */
@Data
public class FormJson {

    private String mode;

    private List<FieldParam> fields;

    private ActionParam submit;

    private RemoteSourceParam remoteValues;

    private Constraint constraint;

    private Map<String, Integer> fieldOrders;

    private List<String> baseFields;

    private List<String> notNeedSummeryColumnIds;

    private Map<String, Object> customConfig;

    public FormJson(String mode){
        this.mode = mode;
        this.fields = Collections.emptyList();
    }

    public FormJson(){
    }

}
