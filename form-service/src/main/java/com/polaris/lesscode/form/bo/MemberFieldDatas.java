package com.polaris.lesscode.form.bo;

import com.polaris.lesscode.form.constant.FormConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author roamer
 * @version v1.0
 * @date 2021/2/1 11:06
 */
@Data
public class MemberFieldDatas {

    private List<MemberFieldData> userList;
    private List<MemberFieldData> roleList;
    private List<MemberFieldData> deptList;

    private List<MemberFieldData> dataList;

    private List<String> invalidList;

    public MemberFieldDatas() {
        userList = new ArrayList<>();
        roleList = new ArrayList<>();
        deptList = new ArrayList<>();

        dataList = new ArrayList<>();
        invalidList = new ArrayList<>();
    }

    public void appendData(MemberFieldData data) {
        if (Objects.isNull(data) || Objects.isNull(data.getType())) {
            return;
        }
        if (Objects.isNull(dataList)) {
            dataList = new ArrayList<>();
        }
        dataList.add(data);
        if (Objects.equals(data.getType(), FormConstant.MEMBER_USER_PREFIX)) {
            appendUser(data);
        } else if (Objects.equals(data.getType(), FormConstant.MEMBER_DEPT_PREFIX)) {
            appendDept(data);
        } else if (Objects.equals(data.getType(), FormConstant.MEMBER_ROLE_PREFIX)) {
            appendRole(data);
        }
    }

    private void appendUser(MemberFieldData data) {
        if (Objects.isNull(userList)) {
            userList = new ArrayList<>();
        }
        userList.add(data);
    }

    private void appendRole(MemberFieldData data) {
        if (Objects.isNull(roleList)) {
            roleList = new ArrayList<>();
        }
        roleList.add(data);
    }

    private void appendDept(MemberFieldData data) {
        if (Objects.isNull(deptList)) {
            deptList = new ArrayList<>();
        }
        deptList.add(data);
    }

    public void appendInvalidItem(String id) {
        if (Objects.isNull(invalidList)) {
            invalidList = new ArrayList<>();
        }
        invalidList.add(id);
    }

    public boolean isEmpty() {
        return !hasUser() && !hasRole() && !hasDept();
    }

    public boolean hasUser() {
        return Objects.nonNull(userList) && !userList.isEmpty();
    }

    public boolean hasRole() {
        return Objects.nonNull(roleList) && !roleList.isEmpty();
    }

    public boolean hasDept() {
        return Objects.nonNull(deptList) && !deptList.isEmpty();
    }

    public boolean hasInvalidListItem() {
        return Objects.nonNull(invalidList) && !invalidList.isEmpty();
    }

    public List<Long> getDistinctRealUserIdList() {
        if (Objects.isNull(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(MemberFieldData::getRealId).distinct().collect(Collectors.toList());
    }

    public List<Long> getDistinctRealRoleIdList() {
        if (Objects.isNull(roleList)) {
            return new ArrayList<>();
        }
        return roleList.stream().map(MemberFieldData::getRealId).distinct().collect(Collectors.toList());
    }

    public List<Long> getDistinctRealDeptIdList() {
        if (Objects.isNull(deptList)) {
            return new ArrayList<>();
        }
        return deptList.stream().map(MemberFieldData::getRealId).collect(Collectors.toList());
    }

}
