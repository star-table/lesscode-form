package com.polaris.lesscode.form.service;

import com.polaris.lesscode.form.bo.PlaceholderContext;
import com.polaris.lesscode.uc.internal.feign.UserCenterProvider;
import com.polaris.lesscode.uc.internal.req.*;
import com.polaris.lesscode.uc.internal.resp.*;
import com.polaris.lesscode.vo.Result;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * UserCenterService
 *
 * @author roamer
 * @version v1.0
 * @date 2021/2/1 14:31
 */
@Service
@Slf4j
public class UserCenterService {

    @Autowired
    private UserCenterProvider userCenterProvider;

    public List<UserInfoResp> getAllUserList(Long orgId, Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyList();
        }
        UserListByIdsReq req = UserListByIdsReq.builder().orgId(orgId).ids(new ArrayList<>(ids)).build();
        Result<List<UserInfoResp>> result = userCenterProvider.getAllUserListByIds(req);
        return result.getData();
    }

    public Map<Long, UserInfoResp> getAllUserMap(Long orgId, Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return getAllUserList(orgId, ids).stream().collect(Collectors.toMap(UserInfoResp::getId, infoResp -> infoResp, (o1, o2) -> o2));
    }

    public List<DeptInfoResp> getAllDeptList(Long orgId, Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyList();
        }
        DeptListByIdsReq req = DeptListByIdsReq.builder().orgId(orgId).ids(new ArrayList<>(ids)).build();
        Result<List<DeptInfoResp>> result = userCenterProvider.getAllDeptListByIds(req);
        return result.getData();
    }


    public Map<Long, DeptInfoResp> getAllDeptMap(Long orgId, Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return getAllDeptList(orgId, ids).stream().collect(Collectors.toMap(DeptInfoResp::getId, infoResp -> infoResp, (o1, o2) -> o2));
    }


    public List<RoleInfoResp> getAllRoleList(Long orgId, Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyList();
        }
        RoleListByIdsReq req = RoleListByIdsReq.builder().orgId(orgId).ids(new ArrayList<>(ids)).build();
        Result<List<RoleInfoResp>> result = userCenterProvider.getAllRoleListByIds(req);
        return result.getData();
    }


    public Map<Long, RoleInfoResp> getAllRoleMap(Long orgId, Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return getAllRoleList(orgId, ids).stream().collect(Collectors.toMap(RoleInfoResp::getId, infoResp -> infoResp, (o1, o2) -> o2));
    }

    public PlaceholderContext getPlaceholderContext(Long orgId, Long userId){
        PlaceholderContext context = new PlaceholderContext();
        context.setOrgId(orgId);
        context.setUserId(userId);

        GetUserDeptIdsReq req = new GetUserDeptIdsReq();
        req.setOrgId(orgId);
        req.setUserId(userId);
        GetUserDeptIdsResp getUserDeptIdsResp = userCenterProvider.getUserDeptIds(req).getData();
        List<Long> deptIds = getUserDeptIdsResp.getDeptIds();
        context.setCurrentDeptIds(deptIds);
        if (CollectionUtils.isNotEmpty(deptIds)){
            GetMemberSimpleInfoReq getMemberSimpleInfoReq = new GetMemberSimpleInfoReq();
            getMemberSimpleInfoReq.setOrgId(orgId);
            getMemberSimpleInfoReq.setType(2);
            List<MemberSimpleInfo> members = userCenterProvider.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
            Map<Long, List<Long>> deptParentChildMap = new HashMap<>();
            Map<Long, Long> deptChildParentMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(members)){
                for (MemberSimpleInfo member: members){
                    List<Long> subDeptIds = deptParentChildMap.computeIfAbsent(member.getParentId(), k -> new ArrayList<>());
                    subDeptIds.add(member.getId());
                    deptChildParentMap.put(member.getId(), member.getParentId());
                }
            }
            {
                List<Long> currentDeptAndSubDeptIds = new ArrayList<>(deptIds);
                Queue<Long> parentIds = new LinkedBlockingQueue<>(deptIds);
                Set<Long> repeat = new HashSet<>(deptIds);
                while(true){
                    Long parentId = parentIds.poll();
                    if (parentId == null){
                        break;
                    }
                    List<Long> childs = deptParentChildMap.get(parentId);
                    if (CollectionUtils.isNotEmpty(childs)){
                        childs.removeIf(repeat::contains);
                        currentDeptAndSubDeptIds.addAll(childs);
                        parentIds.addAll(childs);
                        repeat.addAll(childs);
                    }
                }
                context.setCurrentDeptAndSubDeptIds(currentDeptAndSubDeptIds);
            }
            {
                Queue<Long> childIds = new LinkedBlockingQueue<>(deptIds);
                Set<Long> currentDeptAndParentDeptIds = new HashSet<>(deptIds);
                while (true){
                    Long childId = childIds.poll();
                    if (childId == null){
                        break;
                    }
                    Long parentId = deptChildParentMap.get(childId);
                    if (parentId == null){
                        currentDeptAndParentDeptIds.add(0L);
                        continue;
                    }
                    if (! currentDeptAndParentDeptIds.contains(parentId)){
                        currentDeptAndParentDeptIds.add(parentId);
                        childIds.add(parentId);
                    }
                }
                context.setCurrentDeptAndParentDeptIds(new ArrayList<>(currentDeptAndParentDeptIds));
            }
        }

        // 获取成员
        GetDeptUserIdsReq getDeptUserIdsReq = new GetDeptUserIdsReq();
        getDeptUserIdsReq.setOrgId(orgId);
        Map<Long, List<Long>> deptUserIds = userCenterProvider.getDeptUserIds(getDeptUserIdsReq).getData().getData();
        if (MapUtils.isNotEmpty(deptUserIds)){
            context.setCurrentDeptUserIds(getDeptUserIds(deptUserIds, context.getCurrentDeptIds()));
            context.setCurrentDeptAndSubDeptUserIds(getDeptUserIds(deptUserIds, context.getCurrentDeptAndSubDeptIds()));
        }
        return context;
    }

    private List<Long> getDeptUserIds(Map<Long, List<Long>> deptUserIds, List<Long> deptIds){
        Set<Long> userIds = new HashSet<>();
        if (CollectionUtils.isNotEmpty(deptIds)){
            for (Long deptId: deptIds){
                List<Long> uids = deptUserIds.get(deptId);
                if (CollectionUtils.isNotEmpty(uids)){
                    userIds.addAll(uids);
                }
            }
        }
        return new ArrayList<>(userIds);
    }
}
