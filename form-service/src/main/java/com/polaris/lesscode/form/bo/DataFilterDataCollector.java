package com.polaris.lesscode.form.bo;

import com.polaris.lesscode.uc.internal.resp.DeptInfoResp;
import com.polaris.lesscode.uc.internal.resp.RoleInfoResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.workflow.internal.bo.NodeTree;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 过滤器，数据收集器
 */
@Data
public class DataFilterDataCollector {

    /**
     * 用户数据
     */
    private Map<Long, UserInfoResp> userInfoRespMap;

    /**
     * 部门数据
     */
    private Map<Long, DeptInfoResp> deptInfoRespMap;

    /**
     * 角色数据
     */
    private Map<Long, RoleInfoResp> roleInfoRespMap;

    /**
     * 表单数据,key为appID
     */
    private Map<Long, BizForm> formMap;

    /**
     * 普通关联数据
     */
    private Map<Long, Map<Long, Map<String, Object>>> relateDataMap;

    /**
     * 高级关联数据
     */
    private Map<RelateProInfo, List<Map<String, Object>>> relateProDataMap;

    /**
     * 子表数据
     */
    private Map<String, Map<Object, List<Map<String, Object>>>> subTableDataMap;

    /**
     * 数据流程节点
     **/
    private Map<Long, NodeTree> nodeTrees;

}
