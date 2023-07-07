package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.util.DslUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author roamer
 * @version v1.0
 * @date 2021/3/3 15:55
 */
@RunWith(JUnit4.class)
public class DataFilterServiceTest {
    @Test
    public void testCond() {
        String str = "[{\"name\":\"title\",\"label\":\"标题\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":1,\"props\":{\"disabled\":false, \"fieldSearch\":{\"sort\":1, \"type\":\"formSelect\"}, \"hide\":false, \"isSearch\":true, \"pushMsg\":true, \"required\":false}}},{\"name\":\"code\",\"label\":\"编号\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":1,\"props\":{\"disabled\":true, \"fieldSearch\":{\"sort\":1, \"type\":\"formSelect\"}, \"hide\":false, \"isSearch\":true, \"required\":false}}},{\"name\":\"ownerId\",\"label\":\"负责人\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":11,\"props\":{\"collaboratorRoles\":[\"-1\"], \"limit\":1, \"multiple\":false, \"pushMsg\":true}}},{\"name\":\"issueStatus\",\"label\":\"任务状态\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":40,\"customType\":\"groupSelect\",\"props\":{\"collaboratorRoles\":null, \"default\":7, \"groupSelect\":{\"groupOptions\":[{\"children\":[{\"color\":\"#E1E2E4\", \"fontColor\":\"#5f5f5f\", \"id\":7, \"parentId\":1, \"sort\":1, \"value\":\"未开始\"}], \"color\":\"\", \"fontColor\":\"\", \"id\":1, \"value\":\"未开始\"}, {\"children\":[{\"color\":\"#377AFF\", \"fontColor\":\"#377aff\", \"id\":16, \"parentId\":2, \"sort\":2, \"value\":\"进行中\"}], \"color\":\"\", \"fontColor\":\"\", \"id\":2, \"value\":\"进行中\"}, {\"children\":[{\"color\":\"#45CB7E\", \"fontColor\":\"#54a944\", \"id\":26, \"parentId\":3, \"sort\":3, \"value\":\"已完成\"}], \"color\":\"\", \"fontColor\":\"\", \"id\":3, \"value\":\"已完成\"}], \"options\":[{\"color\":\"#E1E2E4\", \"fontColor\":\"#5f5f5f\", \"id\":7, \"parentId\":1, \"sort\":0, \"value\":\"未开始\"}, {\"color\":\"#377AFF\", \"fontColor\":\"#377aff\", \"id\":16, \"parentId\":2, \"sort\":0, \"value\":\"进行中\"}, {\"color\":\"#45CB7E\", \"fontColor\":\"#54a944\", \"id\":26, \"parentId\":3, \"sort\":0, \"value\":\"已完成\"}]}, \"inputnumber\":{\"accuracy\":\"\", \"percentage\":false, \"required\":false, \"thousandth\":false, \"unique\":false}, \"isText\":false, \"member\":{\"multiple\":false, \"required\":false}, \"multiselect\":{\"options\":null}, \"pushMsg\":true, \"required\":true, \"select\":{\"options\":null}}}},{\"name\":\"planStartTime\",\"label\":\"开始时间\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":2,\"props\":{\"pushMsg\":true}}},{\"name\":\"planEndTime\",\"label\":\"截止时间\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":2,\"props\":{\"pushMsg\":true}}},{\"name\":\"remark\",\"label\":\"描述\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":27,\"props\":{\"collaboratorRoles\":null, \"groupSelect\":null, \"inputnumber\":{\"accuracy\":\"\", \"percentage\":false, \"required\":false, \"thousandth\":false, \"unique\":false}, \"isText\":false, \"member\":{\"multiple\":false, \"required\":false}, \"multiselect\":{\"options\":null}, \"pushMsg\":false, \"required\":false, \"select\":{\"options\":null}}}},{\"name\":\"followerIds\",\"label\":\"关注人\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":11,\"props\":{\"collaboratorRoles\":[\"-1\"], \"multiple\":true, \"pushMsg\":true}}},{\"name\":\"auditorIds\",\"label\":\"确认人\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":11,\"props\":{\"collaboratorRoles\":[\"-1\"], \"multiple\":true, \"pushMsg\":true}}},{\"name\":\"projectObjectTypeId\",\"label\":\"任务栏\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":15,\"customType\":\"select\",\"props\":{\"default\":1, \"disabled\":false, \"isText\":true, \"required\":false, \"select\":{\"options\":[{\"color\":\"\", \"id\":1, \"value\":\"任务\"}]}, \"titleDisabled\":false, \"typeDisabled\":false}}},{\"name\":\"iterationId\",\"label\":\"迭代\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":15,\"customType\":\"select\",\"props\":{\"default\":0, \"disabled\":false, \"isText\":false, \"required\":true, \"select\":{\"options\":null}, \"titleDisabled\":false, \"typeDisabled\":false}}},{\"name\":\"projectId\",\"label\":\"所属项目\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":15,\"customType\":\"select\",\"props\":{\"disabled\":false, \"isText\":false, \"required\":true, \"select\":{\"options\":null}, \"titleDisabled\":false, \"typeDisabled\":false}}},{\"name\":\"parentId\",\"label\":\"父任务ID\",\"writable\":true,\"editable\":true,\"sensitiveFlag\":2,\"field\":{\"type\":1,\"props\":{\"disabled\":true, \"fieldSearch\":{\"sort\":1, \"type\":\"formSelect\"}, \"hide\":true, \"isSearch\":true, \"required\":false}}}]";

        List<FieldParam> list = JSON.parseArray(str, FieldParam.class);
        System.out.println(list);
//        List<Condition> conditions = new ArrayList<>();
//
//        conditions.add(Conditions.equal("delFlag", CommonConsts.FALSE));
//        conditions.addAll(Collections.singleton(new Condition()));
//
//        Condition c = new Condition();
//        c.setType(Conditions.VALUES_IN);
//        c.setValues(new Object[]{"U_${current_user}"});
//        c.setColumn("_field_5_bigData_workReport_day");
//        conditions.add(c);
//        DslUtil.resetReqCondition(conditions);
//        Sql sql = Query.select()
//                .from(new Table("test")).where(Conditions.and(conditions)).toSql();
//        System.out.println(sql.getSql());
    }
}