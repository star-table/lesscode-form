package com.polaris.lesscode.form.resp;

import com.polaris.lesscode.form.dto.Column;
import lombok.Data;

import java.util.List;

@Data
public class ImportValidateResp {

    private List<Column> columns;

    private List<ImportValidateInfo> infos;
}
