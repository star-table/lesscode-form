package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.vo.Result;
import com.polaris.lesscode.workflow.internal.api.WorkflowApi;
import com.polaris.lesscode.workflow.internal.bo.NodeTree;
import com.polaris.lesscode.workflow.internal.req.GetNodesByDataIdsReq;
import com.polaris.lesscode.workflow.internal.req.GetNodesByIdsReq;
import com.polaris.lesscode.workflow.internal.req.WorkflowSponsorReq;
import com.polaris.lesscode.workflow.internal.resp.NodeResp;
import com.polaris.lesscode.workflow.internal.resp.TodoResp;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流service
 *
 * @author Nico
 * @date 2021/3/31 14:31
 */
@Slf4j
@Service
public class WorkflowService {

    @Autowired
    private WorkflowApi workflowApi;

    public List<TodoResp> getUserTodos(Long orgId, Long userId, Long appId, Long dataId, Integer type, Integer status){
        return workflowApi.getUserTodos(orgId, userId, appId, dataId, type, status).getData();
    }

    public List<NodeTree> getNodesByIds(List<Long> nodeIds){
        GetNodesByIdsReq req = new GetNodesByIdsReq();
        req.setNodeIds(nodeIds);
        return workflowApi.getNodesByIds(req).getData();
    }

    public Map<Long, NodeTree> getNodesByDataIds(List<Long> dataIds){
        GetNodesByDataIdsReq req = new GetNodesByDataIdsReq();
        req.setDataIds(dataIds);
        return workflowApi.getNodesByDataIds(req).getData();
    }

    public void sponsor(Long orgId, Long userId, Long appId, Long dataId){
        WorkflowSponsorReq req = new WorkflowSponsorReq();
        req.setOrgId(orgId);
        req.setUserId(userId);
        req.setAppId(appId);
        req.setDataId(dataId);
        try{
            workflowApi.sponsor(req).getData();
        }catch (Exception e){
            log.error("发起流程审批失败，请求体 {}，错误信息 {}", JSON.toJSONString(req), e.getMessage());
        }
    }
}
