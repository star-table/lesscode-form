package com.polaris.lesscode.form.internal.sula;

import lombok.Data;

import java.util.Map;

/**
 * @Author Liu.B.J
 */
@Data
public class RemoteSourceParam {

    private String url;

    private String method;

    private Boolean successMessage;

    private Map<String, Object> params;

}
