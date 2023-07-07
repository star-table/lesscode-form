package com.polaris.lesscode.form.openapi.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateValuesReq {

    private List<Map<String, Object>> values;
}
