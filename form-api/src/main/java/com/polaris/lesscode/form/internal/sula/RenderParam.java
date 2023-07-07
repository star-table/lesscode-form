package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

import java.util.List;

/**
 * @Author Liu.B.J
 */
@Data
public class RenderParam {

    private String type;
    private Prop props;
    private List<Object> action;
    private String tooltip;
    private String confirm;
    private String visible;


}
