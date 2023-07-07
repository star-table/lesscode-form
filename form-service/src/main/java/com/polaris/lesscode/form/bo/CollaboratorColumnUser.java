package com.polaris.lesscode.form.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorColumnUser {
    private Long appId;
    private Long orgId;
    private Long userId;
    private Long tableId;
    private String fieldName;
    private Long deptId;
}
