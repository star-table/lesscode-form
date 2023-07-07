package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.List;

@Data
public class PlaceholderContext {

    private Long userId;

    private Long orgId;

    private List<Long> currentDeptIds;

    private List<Long> currentDeptAndSubDeptIds;

    private List<Long> currentDeptUserIds;

    private List<Long> currentDeptAndSubDeptUserIds;

    private List<Long> currentDeptAndParentDeptIds;


}
