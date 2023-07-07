package com.polaris.lesscode.form.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * import process
 *
 * @author ethanliao
 * @date 2020/12/31
 * @since 1.0.0
 */
@Data
@ApiModel("导入进度回执")
public class ImportProcessResp {
    private String dataCount;
    private String insertCount;
    private String updateCount;
    private String currentIdx;
}
