package com.polaris.lesscode.form.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 过滤器资源收集，资源特制再真正查询数据前必要的信息
 */
@Data
public class DataFilterResourceCollector {

    /**
     * 相关的appId
     */
    private Set<Long> relevantAppIds;

    /**
     * 相关的用户id
     */
    private Set<Long> relevantUserIds;

    /**
     * 相关的部门id
     */
    private Set<Long> relevantDeptIds;

    /**
     * 相关的角色id
     */
    private Set<Long> relevantRoleIds;

    /**
     * 工作流节点ID数组
     **/
    private Set<Long> workflowNodeIds;

    /**
     * 关联的id字典，key为引用表名，value为被引用数据的id列表
     */
    private Map<RelateInfo, List<Long>> relateDataIdsMap;

    /**
     * 高级关联的条件数据
     */
    private Map<RelateProInfo, List<Object>> relateProConditionDataMap;
}
