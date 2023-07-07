package com.polaris.lesscode.form.service;

import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Conditions;
import com.polaris.lesscode.form.bo.AppAuthorityContext;
import com.polaris.lesscode.permission.internal.enums.FieldFilterMethod;
import com.polaris.lesscode.permission.internal.enums.FormFieldAuthCode;
import com.polaris.lesscode.permission.internal.feign.AppPermissionProvider;
import com.polaris.lesscode.permission.internal.model.bo.DataAuthBo;
import com.polaris.lesscode.permission.internal.model.resp.AppAuthorityResp;
import com.polaris.lesscode.permission.internal.model.resp.FromPerOptAuthVO;
import com.polaris.lesscode.vo.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionService {
    @Autowired
    private AppPermissionProvider permissionProvider;

    @Autowired
    private AppApi appApi;

    public AppAuthorityContext appAuth(long orgId, long appId, Long userId){
//        AppResp targetApp = appApi.getAuthExtendsApp(appId).getData();
        // TODO 之后权限要判断的应用id应该用targetApp的id
        AppAuthorityResp appAuthorityResp =  permissionProvider.getAppAuthority(orgId, appId, null, userId).getData();
        return new AppAuthorityContext(appAuthorityResp);
    }

    public FromPerOptAuthVO opAuth(long orgId, long appId, Long userId) {
        FromPerOptAuthVO optAuth = permissionProvider.getOptAuth(orgId, appId, userId).getData();
        return optAuth;
    }

    public List<String> opAuthList(long orgId, long appId, Long userId) {
        List<String> list = permissionProvider.getOptAuthList(orgId, appId, userId).getData();
        return list;
    }

    public <T> T fieldAuth(long orgId, long appId, Long userId, T input) {
        // 过滤字段权限
//        Map<String, Integer> fieldAuth = permissionProvider.getFieldAuth(orgId, appId, userId).getData();

        // mock
//        Map<String, Integer> fieldAuth = new HashMap<>();
//        fieldAuth.put("_field_1599794988497", 1);
//        fieldAuth.put("_field_1599794988499", 4);

//        if (input instanceof Page) {
//            ((Page) input).getList().stream().map(item -> {
//                Iterator<Map.Entry<String, Object>> iter = ((Map<String, Object>) item).entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, Object> entry = iter.next();
//                    String key = entry.getKey();
//                    if (fieldAuth.containsKey(key)) {
//                        FormFieldAuthCode fieldAuthCode = FormFieldAuthCode.forValue(fieldAuth.get(key));
//                        if (fieldAuthCode.isMasking()) {
//                            entry.setValue("****");
//                        }
//                    } else if(! "id".equals(key) && ! "recycleFlag".equals(key) && ! "recycleTime".equals(key)) {
//                        iter.remove();
//                    }
//                }
//                return item;
//            }).collect(Collectors.toList());
//        } else if (input instanceof Map) {
//
//        }
        return input;

    }


    public List<Condition> dataAuth(long orgId, long appId, Long userId) {
        return permissionProvider.getDataAuth(orgId, appId, userId).getData();
    }

    public Condition mergeList(List<Condition> conditions) {
        if (!CollectionUtils.isEmpty(conditions)) {
            Condition cond = new Condition();
            cond.setType(Conditions.OR);
            cond.setConds(conditions.toArray(new Condition[]{}));
            return cond;
        }
        return null;
    }

    public Condition translateDataAuth2Condition(List<DataAuthBo> dataAuthBos) {
        Condition cond = new Condition();
        cond.setType(Conditions.OR);
        List<Condition> subConds = new ArrayList<>();
        for (DataAuthBo dataAutoBo : dataAuthBos) {
            String rel = dataAutoBo.getRel();
            Condition filter = new Condition();
            filter.setType(rel);
            List<DataAuthBo.FilterCondItem> filterCondItems = dataAutoBo.getCond();
            List<Condition> filterConds = new ArrayList<>();
            for (DataAuthBo.FilterCondItem filterCondItem : filterCondItems) {
                Condition filterCond = new Condition();
                filterCond.setColumn(filterCondItem.getField());

                filterCond.setType(filterCondItem.getMethod());
                filterCond.setValues(filterCondItem.getValue().toArray());

                FieldFilterMethod method = FieldFilterMethod.forValue(filterCondItem.getMethod());

                switch (method) {
                    case EQ:
                        filterCond.setType(Conditions.EQUAL);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case NE:
                        filterCond.setType(Conditions.UN_EQUAL);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case GT:
                        filterCond.setType(Conditions.GT);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case GE:
                        filterCond.setType(Conditions.GTE);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case LT:
                        filterCond.setType(Conditions.LT);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case LE:
                        filterCond.setType(Conditions.LTE);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case IN:
                        filterCond.setType(Conditions.IN);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case NIN:
                        filterCond.setType(Conditions.NOT_IN);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case LIKE:
                        filterCond.setType(Conditions.LIKE);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case UNLIKE:
                        filterCond.setType(Conditions.NOT_LIKE);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    case EMPTY:
                        filterCond.setType(Conditions.EQUAL);
                        filterCond.setValues(new String[]{""});
                        break;
                    case NOT_EMPTY:
                        filterCond.setType(Conditions.UN_EQUAL);
                        filterCond.setValues(new String[]{""});
                        break;
                    case RANGE:
                        filterCond.setType(Conditions.BETWEEN);
                        filterCond.setLeft(filterCondItem.getValue().get(0));
                        filterCond.setRight(filterCondItem.getValue().get(1));
                        break;
                    case FORMULA:

                        break;
                    case ALL:
                        // select data::jsonb->>'dept' from form where data::jsonb->'dept' ?&  ARRAY['a','b'];
                        filterCond.setType(Conditions.ALL_IN);
                        filterCond.setValues(filterCondItem.getValue().toArray());
                        break;
                    default:
                        break;
                }

                filterConds.add(filterCond);
            }
            filter.setConds(filterConds.toArray(new Condition[]{}));
            subConds.add(filter);
        }

        cond.setConds(subConds.toArray(new Condition[]{}));
        return cond;
    }
}
