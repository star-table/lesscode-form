package com.polaris.lesscode.form.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppRecycleReq {

    private Long orgId;

    private Long userId;

    private Long appId;

    private List<Long> issueIds;

    private List<Long> dataIds;

    private Long tableId;
}
