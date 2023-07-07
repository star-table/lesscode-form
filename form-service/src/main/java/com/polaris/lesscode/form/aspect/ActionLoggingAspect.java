package com.polaris.lesscode.form.aspect;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.app.internal.enums.ActionObjType;
import com.polaris.lesscode.app.internal.enums.ActionType;
import com.polaris.lesscode.app.internal.feign.AppActionProvider;
import com.polaris.lesscode.app.internal.req.CreateAppActionChanges;
import com.polaris.lesscode.app.internal.req.CreateAppActionReq;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.dc.internal.dsl.Conditions;
import com.polaris.lesscode.dc.internal.dsl.Query;
import com.polaris.lesscode.dc.internal.dsl.SqlUtil;
import com.polaris.lesscode.dc.internal.dsl.Table;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.form.annotations.ActionLogging;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.service.GoTableService;
import com.polaris.lesscode.form.util.PlaceHolderHelper;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;
import com.polaris.lesscode.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 自动缓存切面
 *
 * @date 2020年1月19日
 */
@Aspect
@Component
@Slf4j
public class ActionLoggingAspect {

//	@Autowired
//	private AppFormMapper appFormMapper;

	@Autowired
	private GoTableService goTableService;

	@Autowired
	private DataCenterProvider dataCenterProvider;

	@Autowired
	private AppActionProvider appActionProvider;

	private static final PlaceHolderHelper PLACEHOLDER = new PlaceHolderHelper("${", "}");

	private static final Map<String, Object> EMPTY_MAP = new HashMap<>();

	@Pointcut("@annotation(com.polaris.lesscode.form.annotations.ActionLogging)")
	public void pointCut() {

	}

	@Around("pointCut()")
	public Object arround(ProceedingJoinPoint point) throws Throwable{
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();

		String[] methodArgNames = signature.getParameterNames();
		Object[] methodArgs = point.getArgs();
		Map<String, Object> methodArgValues = new HashMap<>();
		if(! ArrayUtils.isEmpty(methodArgNames)) {
			for(int index = 0; index < methodArgNames.length; index ++) {
				methodArgValues.put(methodArgNames[index], methodArgs[index]);
			}
		}

		ActionLogging actionLogging = method.getAnnotation(ActionLogging.class);
		ActionLogger actionLogger = new ActionLogger(actionLogging, methodArgValues);

		Object result;
		try{
			if(actionLogger.isEnabledLogging()){
				actionLogger.before();
			}
		}catch (Exception e){
			log.error("actionLogger.before err", e);
		}finally {
			result = point.proceed();
			try{
				if(actionLogger.isEnabledLogging()){
					actionLogger.after();
				}
			}catch (Exception e){
				log.error("actionLogger.after err", e);
			}
		}
		return result;
	}

	private boolean assertActionLogging(ActionLogging actionLogging){
		return ! (StringUtils.isBlank(actionLogging.appIdExpress())
				|| (StringUtils.isBlank(actionLogging.dataIdExpress()) && StringUtils.isBlank(actionLogging.dataExpress()))
				|| RequestContext.currentOrgId() == null
				|| RequestContext.currentUserId() == null);
	}

	private class ActionLogger{

		protected ActionType actionType;

		protected ActionObjType objType;

		protected List<Long> subDataIds;

		protected List<Long> dataIds;

		protected List<Map<String, Object>> datas;

		protected CreateAppActionReq createAppActionReq;

		protected Long orgId;

		protected Long appId;

		protected Long operatorId;

		protected String subformKey;

		protected boolean enabledLogging;

		public ActionLogger(ActionLogging actionLogging, Map<String, Object> methodArgValues){
			if(assertActionLogging(actionLogging)){
				this.orgId = RequestContext.currentOrgId();
				this.operatorId = RequestContext.currentUserId();
				this.actionType = actionLogging.action();
				this.objType = actionLogging.objType();
				Object subDataIdsObject = PLACEHOLDER.parseExpress(methodArgValues, actionLogging.subDataIdExpress());
				Object dataIdsObject = PLACEHOLDER.parseExpress(methodArgValues, actionLogging.dataIdExpress());
				if(subDataIdsObject instanceof Collection){
					this.subDataIds = new ArrayList<>((Collection) subDataIdsObject);
				}else if(subDataIdsObject instanceof Number){
					this.subDataIds = new ArrayList<>();
					this.subDataIds.add(((Number) subDataIdsObject).longValue());
				}
				if(dataIdsObject instanceof Collection){
					this.dataIds = new ArrayList<>((Collection) dataIdsObject);
				}else if(dataIdsObject instanceof Number){
					this.dataIds = new ArrayList<>();
					this.dataIds.add(((Number) dataIdsObject).longValue());
				}
				if(StringUtils.isNotBlank(actionLogging.dataExpress())){
					Object datasObject = PLACEHOLDER.parseExpress(methodArgValues, actionLogging.dataExpress());
					if(datasObject instanceof Collection){
						this.datas = new ArrayList<>((Collection) datasObject);
					}else if(datasObject instanceof Map){
						this.datas = new ArrayList<>();
						this.datas.add((Map) datasObject);
					}
				}
				if(StringUtils.isNotBlank(actionLogging.appIdExpress())){
					Object appIdObject = PLACEHOLDER.parseExpress(methodArgValues, actionLogging.appIdExpress());
					if(appIdObject instanceof Number){
						this.appId = ((Number) appIdObject).longValue();
					}
				}
				if(StringUtils.isNotBlank(actionLogging.subformKeyExpress())){
					Object subformKeyObject = PLACEHOLDER.parseExpress(methodArgValues, actionLogging.subformKeyExpress());
					if(subformKeyObject instanceof String){
						this.subformKey = String.valueOf(subformKeyObject);
					}
				}
				if(appId != null && appId > 0 && (CollectionUtils.isNotEmpty(this.dataIds) || CollectionUtils.isNotEmpty(this.datas))){
					this.enabledLogging = true;
				}
			}
		}

		public boolean isEnabledLogging(){
			return this.enabledLogging;
		}

		public void before(){
			createAppActionReq = new CreateAppActionReq();
			createAppActionReq.setAction(actionType.getCode());
			createAppActionReq.setObjType(objType.getCode());
			createAppActionReq.setObjId(appId);

			if(Objects.equals(objType, ActionObjType.FORM)){
				boolean isSubform = StringUtils.isNotBlank(subformKey);
				String subformName = StringUtils.EMPTY;

				TableSchemas tableResp = goTableService.readSchemaByAppId(this.appId, orgId, 0L);
				if(tableResp == null){
					log.error("ActionLogging Aspect appFormMapper.getByAppId 失败，对应的表单不存在, appId {}", appId);
					return;
				}
				List<FieldParam> fields = new ArrayList<>();
				if (tableResp.getColumns() != null) {
					tableResp.getColumns().forEach(f -> {
						FieldParam fp  = JSON.toJavaObject(f,FieldParam.class);
						fields.add(fp);
					});
				}
				if(isSubform){
//					for(FieldParam field: fields){
//						if(Objects.equals(field.getName(), subformKey) && Objects.equals(field.getField().getType(), FieldTypeEnums.SUBFORM.getFormFieldType())){
//							fields = field.getFields();
//							subformName = field.getLabel();
//							break;
//						}
//					}
				}

				Map<String, String> names = new LinkedHashMap<>();
				Map<String, String> types = new LinkedHashMap<>();
				if(CollectionUtils.isNotEmpty(fields)){
					for (FieldParam field: fields){
						names.put(field.getName(), field.getLabel());
						types.put(field.getName(), field.getField().getType());
					}
				}
				createAppActionReq.setNames(names);
				createAppActionReq.setTypes(types);

				List<CreateAppActionChanges> changesList = new ArrayList<>();
				if (Objects.equals(actionType, ActionType.CREATE) && CollectionUtils.isNotEmpty(datas)){
					for(Map data: datas){
						CreateAppActionChanges changes = new CreateAppActionChanges();
						changes.setBefore(EMPTY_MAP);
						changes.setAfter(data);
						changesList.add(changes);

						if(isSubform){
							changes.setSubformKey(subformKey);
							changes.setSubformName(subformName);
							changes.setDataId(dataIds.get(0));
						}
					}
				} else if (Objects.equals(actionType, ActionType.MODIFY) && CollectionUtils.isNotEmpty(datas)){
					List<Long> updatedDataIds = new ArrayList<>();
					Map<Long, Map<String, Object>> updatedDatas = new HashMap<>();
					for(Map data: datas){
						if(data.containsKey("id")){
							Long dataId = Long.valueOf(String.valueOf(data.get("id")));
							updatedDataIds.add(dataId);

							Map<String, Object> updatedData = new HashMap<String, Object>(data);
							updatedData.remove("id");
							updatedDatas.put(dataId, updatedData);
						}
					}

					String tableName = SqlUtil.wrapperTableName(orgId, tableResp.getTableId());
					if(isSubform){
						tableName = SqlUtil.wrapperSubTableName(tableName, subformKey.split(CommonConsts.KEY_NAME_PREFIX)[1]);
					}

					if(CollectionUtils.isNotEmpty(updatedDataIds)){
						List<Map<String, Object>> beforeDatas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), Query.select()
								.from(new Table(tableName))
								.where(Conditions.and(
										Conditions.in("id", updatedDataIds.toArray())
								))).getData();

						if(CollectionUtils.isNotEmpty(beforeDatas)){
							for (Map<String, Object> data: beforeDatas){
								Long dataId = Long.valueOf(String.valueOf(data.get("id")));

								CreateAppActionChanges changes = new CreateAppActionChanges();
								Map<String, Object> afterData = updatedDatas.get(dataId);
								Map<String, Object> beforeData = new HashMap<>();
								if(afterData != null){
									for(String key: afterData.keySet()){
										beforeData.put(key, data.get(key));
									}
								}
								changes.setBefore(beforeData);
								changes.setAfter(afterData);
								if(isSubform){
									changes.setSubformKey(subformKey);
									changes.setSubformName(subformName);
									changes.setDataId(dataIds.get(0));
									changes.setSubformDataId(dataId);
								}else{
									changes.setDataId(dataId);
								}
								if (MapUtils.isNotEmpty(beforeData) || MapUtils.isNotEmpty(afterData)){
									changesList.add(changes);
								}
							}
						}
					}
				} else if (Objects.equals(actionType, ActionType.DELETE) ||
						Objects.equals(actionType, ActionType.RECYCLE) ||
						Objects.equals(actionType, ActionType.RECOVER) ||
						Objects.equals(actionType, ActionType.ENABLE) ||
						Objects.equals(actionType, ActionType.DISABLE)){
					List<Long> deletedIds = isSubform ? subDataIds : dataIds;
					for(Long dataId: deletedIds){
						CreateAppActionChanges changes = new CreateAppActionChanges();
						changes.setBefore(EMPTY_MAP);
						changes.setAfter(EMPTY_MAP);
						changesList.add(changes);
						if(isSubform){
							changes.setSubformKey(subformKey);
							changes.setSubformName(subformName);
							changes.setDataId(dataIds.get(0));
							changes.setSubformDataId(dataId);
						}else{
							changes.setDataId(dataId);
						}
					}
				}
				createAppActionReq.setChanges(changesList);
			}
		}

		public void after(){
			if(Objects.equals(objType, ActionObjType.FORM)){
				if (Objects.equals(actionType, ActionType.CREATE) && CollectionUtils.isNotEmpty(createAppActionReq.getChanges())){
					for(CreateAppActionChanges changes: createAppActionReq.getChanges()){
						Long dataId = Long.valueOf(String.valueOf(changes.getAfter().get("id")));
						if(StringUtils.isNotBlank(changes.getSubformKey())){
							changes.setSubformDataId(dataId);
						}else{
							changes.setDataId(dataId);
						}
					}
				}
				if(CollectionUtils.isNotEmpty(createAppActionReq.getChanges())){
					appActionProvider.createAction(orgId, operatorId, createAppActionReq);
				}
			}
		}
	}

}
