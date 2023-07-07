package com.polaris.lesscode.form.resp;

import com.polaris.lesscode.form.dto.Column;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * import sample resp
 *
 * @author ethanliao
 * @date 2020/12/30
 * @since 1.0.0
 */
@Data
@ApiModel(value = "导入预览", description = "导入预览")
public class ImportSampleResp {
    private List<Column> columns;

    private List<Map<Integer, Object>> sample;
}
