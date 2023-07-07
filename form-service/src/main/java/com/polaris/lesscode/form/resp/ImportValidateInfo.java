package com.polaris.lesscode.form.resp;

import lombok.Data;

import java.util.Map;

@Data
public class ImportValidateInfo {

    private int index;

    private Map<String, Object> data;

    private Map<String, String> errorMsg;
}
