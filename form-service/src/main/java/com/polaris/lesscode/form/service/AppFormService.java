package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.feign.AppVersionProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.bo.AppAuthorityContext;
import com.polaris.lesscode.form.bo.BizForm;
import com.polaris.lesscode.form.bo.FieldAuthBo;
import com.polaris.lesscode.form.internal.sula.FormJson;
import com.polaris.lesscode.form.constant.CommonField;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.entity.AppForm;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.*;
import com.polaris.lesscode.form.mapper.AppFormMapper;
import com.polaris.lesscode.form.resp.AppFormConfigResp;
import com.polaris.lesscode.form.resp.AppFormFieldResp;
import com.polaris.lesscode.form.resp.AppFormResp;
import com.polaris.lesscode.form.util.PermissionUtil;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.enums.FormFieldAuthCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppFormService extends ServiceImpl<AppFormMapper, AppForm>{

	@Autowired
	private AppFormMapper appFormMapper;

	@Autowired
	private AppProvider appProvider;

	@Autowired
	private AppSummaryService summaryService;

	@Autowired
	private AppVersionProvider appVersionProvider;

	@Autowired
	private DataCenterProvider dataCenterProvider;

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private PermissionService permissionService;

	//private static final String fieldPrefix  = "_field_";

	/**
	 * 获取应用表单信息
	 *
	 * @param appId 应用id
	 * @param drafted 是否获取草稿
	 * @return
	 */
	public AppFormResp get(Long appId, Long orgId, Long userId, boolean drafted) {
		return null;
//		AppResp appResp = appProvider.getAppInfo(orgId, appId).getData();
//		if (appResp == null) {
//			throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
//		}
//
//		AppAuthorityContext appAuthorityContext = permissionService.appAuth(orgId, appId, userId);
//		if (! appAuthorityContext.getAppAuthorityResp().hasAppOptAuth(OperateAuthCode.HAS_READ.getCode())){
//			throw new BusinessException(ResultCode.FORM_OP_NO_READ);
//		}
//
//		Map<String, FormFieldAuthCode> formFieldAuthCodeMap = permissionUtil.getFieldAuth(orgId, appId, userId);
//		Map<String, FieldAuthBo> fieldAuthBoMap = new HashMap<>();
//		formFieldAuthCodeMap.forEach((k, v) -> {
//			FieldAuthBo bo = new FieldAuthBo();
//			bo.setReadable(v.isRead());
//			bo.setEditable(v.isRead());
//			bo.setMasking(v.isMasking());
//			fieldAuthBoMap.put(k, bo);
//		});
//
//		BizForm bizForm = summaryService.getBizForm(orgId, appId, null);
//		TableJson tableJson = new TableJson();
//		if (CollectionUtils.isNotEmpty(bizForm.getFieldList())) {
//			List<FieldParam> fields = bizForm.getFieldList();
//			fields.removeIf(f -> FormFieldConstant.isCommonField(f.getName()));
//			List<Column> columns = fields.stream().map(p -> {
//				Column c = new Column();
//				if (!CollectionUtils.isEmpty(p.getFields())) {
//					// 子表单
//					List<Children> childrens = p.getFields().stream().map(p2 -> {
//						Children cr = new Children();
//						cr.setKey(p2.getName());
//						cr.setTitle(p2.getLabel());
//						Field f = p2.getField();
//						if(f != null){
//							FieldTypeEnums fieldTypeEnums = FieldTypeEnums.formatByFieldType(f.getType());
//							if(fieldTypeEnums == null){
//								log.info("type not exist: {}", f.getType());
//								throw new BusinessException(ResultCode.FIELD_TYPE_NOT_EXIST);
//							}
//							f.setDataType(fieldTypeEnums.getType());
//						}
//						cr.setField(f);
//						cr.setUnique(p2.getUnique());
//						cr.setRules(p2.getRules());
//						return cr;
//					}).collect(Collectors.toList());
//
//					childrens.addAll(CommonField.commonChildrens);
//
//					if(p.getContainer() != null && p.getContainer().getProps() != null){
//						c.setTitle(p.getContainer().getProps().getTitle());
//					}
//					c.setKey(p.getName());
//					c.setTitle(p.getLabel());
//					c.setEnTitle(p.getEnLabel());
//					c.setAliasTitle(p.getAliasLabel());
//					c.setChildren(childrens);
//				}else{
//					c.setTitle(p.getLabel());
//					c.setEnTitle(p.getEnLabel());
//					c.setAliasTitle(p.getAliasLabel());
//					c.setKey(p.getName());
//				}
//				Field f = p.getField();
//				if(f != null){
//					FieldTypeEnums fileType = FieldTypeEnums.formatByFieldType(f.getType());
//					if (fileType != null) {
//						f.setDataType(fileType.getType());
//					}
//					if (f.getType().equals(FieldTypeEnums.RELATION_TABLE_PRO.getDataType())) {
//						JSONObject relateTableProJSONObject = (JSONObject) f.getProps().get(FieldTypeEnums.RELATION_TABLE_PRO.getDataType());
//						RelateTableProFieldConfig relateTableProFieldConfig = JSON.parseObject(relateTableProJSONObject.toJSONString(), RelateTableProFieldConfig.class);
//						String linkCondFieldName = relateTableProFieldConfig.getLinkCond().getName();
//						String linkShowFieldName = relateTableProFieldConfig.getLinkShow().getName();
//
//						Long destRelateTableAppId = relateTableProFieldConfig.getAppId();
//						AppForm relateAppForm = appFormMapper.selectOne(new LambdaQueryWrapper<AppForm>().eq(AppForm::getAppId, destRelateTableAppId).eq(AppForm::getDelFlag, CommonConsts.NO_DELETE).last(" limit 1"));
//						if (relateAppForm == null) {
//							throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST.getCode(), "表单字段配置错误:" + p.getLabel());
//						}
//						String relateFormConfig = relateAppForm.getConfig();
//						if (StringUtils.isNotEmpty(relateFormConfig)) {
//							// config-->FormJson
//							FormJson relateFormJson = JSON.parseObject(relateFormConfig, FormJson.class);
//							List<FieldParam> relateFormFields = relateFormJson.getFields();
//							if (!CollectionUtils.isEmpty(relateFormFields)) {
//								for (FieldParam rf : relateFormFields) {
//									if (rf.getName().equals(linkCondFieldName)) {
//										relateTableProFieldConfig.setLinkCond(rf);
//									} else if (rf.getName().equals(linkShowFieldName)) {
//										relateTableProFieldConfig.setLinkShow(rf);
//									}
//								}
//							}
//						}
//						f.getProps().put(FieldTypeEnums.RELATION_TABLE_PRO.getDataType(), relateTableProFieldConfig);
//					}else if (f.getType().equals(FieldTypeEnums.RELATION_TABLE.getDataType())) {
//						JSONObject relateTableJSONObject = (JSONObject) f.getProps().get(FieldTypeEnums.RELATION_TABLE.getDataType());
//						RelateTableFieldConfig relateTableFieldConfig = JSON.parseObject(relateTableJSONObject.toJSONString(), RelateTableFieldConfig.class);
//						Long destRelateTableAppId = relateTableFieldConfig.getAppId();
//						AppForm relateAppForm = appFormMapper.selectOne(new LambdaQueryWrapper<AppForm>().eq(AppForm::getAppId, destRelateTableAppId).eq(AppForm::getDelFlag, CommonConsts.NO_DELETE).last(" limit 1"));
//						if (relateAppForm == null) {
//							throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST.getCode(), "表单字段配置错误:" + p.getLabel());
//						}
//						String relateFormConfig = relateAppForm.getConfig();
//						if (StringUtils.isNotEmpty(relateFormConfig)) {
//							// config-->FormJson
//							FormJson relateFormJson = JSON.parseObject(relateFormConfig, FormJson.class);
//							List<FieldParam> relateFormFields = relateFormJson.getFields();
//							relateTableFieldConfig.setColumns(relateFormFields);
//						}
//						f.getProps().put(FieldTypeEnums.RELATION_TABLE.getDataType(), relateTableFieldConfig);
//					}
//				}
//				c.setField(f);
//				c.setUnique(p.getUnique());
//				return c;
//			}).collect(Collectors.toList());
//
//			// add 公共字段
//			columns.addAll(FormFieldConstant.getCommonColumns().values());
//			fields.addAll(FormFieldConstant.getCommonFields().values());
//			tableJson.setColumns(columns);
//			tableJson.setFields(fields);
//		}
//
//		String config = JSON.toJSONString(tableJson, SerializerFeature.DisableCircularReferenceDetect);
//
//		AppFormResp resp = new AppFormResp();
//		resp.setName(appResp.getName());
//		resp.setAppId(appId);
//		resp.setConfig(config);
//		resp.setFieldAuths(fieldAuthBoMap);
//		return resp;
	}

	public AppFormResp get(Long appId){
		AppForm appForm = appFormMapper.selectOne(new LambdaQueryWrapper<AppForm>().eq(AppForm :: getAppId, appId).eq(AppForm :: getDelFlag, CommonConsts.NO_DELETE).last(" limit 1"));
		if(appForm == null) {
			throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
		}
		return ConvertUtil.convert(appForm, AppFormResp.class);
	}

	/**
	 * 获取表单字段信息
	 *
	 * @param appId
	 * @param orgId
	 * @param userId
	 * @return
	 */
	public List<FieldParam> getFields(Long appId, Long orgId, Long userId){
		AppResp appResp = appProvider.getAppInfo(orgId, appId).getData();
		if(appResp == null) {
			throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
		}
		BizForm bizForm = summaryService.getBizForm(orgId, appId, null);
		if(bizForm.getFieldList() != null){
			bizForm.getFieldList().addAll(FormFieldConstant.getCommonFields().values());
		}
		return bizForm.getFieldList();
	}

	public List<AppFormFieldResp> getFieldType(){
		List<AppFormFieldResp> data = new ArrayList<>();
		FieldTypeEnums[] enums = FieldTypeEnums.values();
		for (FieldTypeEnums _enu : enums) {
			AppFormFieldResp appFormFieldResp = new AppFormFieldResp();
			appFormFieldResp.setCode(_enu.getCode());
			appFormFieldResp.setName(_enu.getDesc());
			appFormFieldResp.setType(_enu.getFormFieldType());
			data.add(appFormFieldResp);
		}
		return data;
	}

	public List<AppForm> getFormList(Long orgId, Collection<Long> appIds) {
		if (Objects.isNull(appIds) || appIds.isEmpty()) {
			return Collections.emptyList();
		}
		return list(new LambdaQueryWrapper<AppForm>().eq(AppForm::getOrgId, orgId).in(AppForm::getAppId, appIds));
	}

	public void updateFormConfig(Long orgId, Long appId, List<FieldParam> fieldParams){
		AppForm appForm = appFormMapper.getByAppId(appId);
		if (appForm == null) {
			throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
		}
		FormJson config = JSON.parseObject(appForm.getConfig(), FormJson.class);
		config.setFields(fieldParams);
		AppForm updated = new AppForm();
		updated.setId(appForm.getId());
		updated.setConfig(JSON.toJSONString(config));
		appFormMapper.updateById(updated);
	}

	public AppFormConfigResp getFormConfig(Long orgId, Long appId, boolean hiddenCommonField){
		BizForm bizForm = summaryService.getBizForm(orgId, appId, null);
		return getFormConfig(bizForm, hiddenCommonField);
	}

	public List<AppFormConfigResp> getFormConfigs(Long orgId, List<Long> appIds, boolean hiddenCommonField){
		List<BizForm> bizForms = summaryService.getBizForms(orgId, appIds);
		List<AppFormConfigResp> formConfigs = new ArrayList<>();
		for (BizForm bizForm: bizForms){
			formConfigs.add(getFormConfig(bizForm, hiddenCommonField));
		}
		return formConfigs;
	}

	public AppFormConfigResp getFormConfig(BizForm bizForm, boolean hiddenCommonField){
		long orgId = bizForm.getOrgId();
		List<FieldParam> fieldParams = bizForm.getFieldList();

		AppFormConfigResp appFormConfigResp = new AppFormConfigResp();

		// views
		appFormConfigResp.setFieldOrders(bizForm.getConfig().getFieldOrders());

		// 相关的应用
		List<AppResp> relevantApps = new ArrayList<>();
		List<BizForm> relevantBizForms = new ArrayList<>();
		List<Long> relevantAppIds = fieldParams.stream()
				.filter(fieldParam ->
					(Objects.equals(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType(), fieldParam.getField().getType())
							|| Objects.equals(FieldTypeEnums.RELATION_TABLE.getFormFieldType(), fieldParam.getField().getType()))
							&& MapUtils.isNotEmpty(fieldParam.getField().getProps())
				).map(fieldParam -> {
					if (Objects.equals(FieldTypeEnums.RELATION_TABLE.getFormFieldType(), fieldParam.getField().getType())){
						RelateTableFieldConfig referenceDataFieldConfig = JSON.parseObject(JSON.toJSONString(fieldParam.getField().getProps().get(FieldTypeEnums.RELATION_TABLE.getFormFieldType())), RelateTableFieldConfig.class);
						return referenceDataFieldConfig.getAppId();
					}else{
						RelateTableProFieldConfig relateProConfig = JSON.parseObject(JSON.toJSONString(fieldParam.getField().getProps().get(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())), RelateTableProFieldConfig.class);
						return relateProConfig.getAppId();
					}
				}).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(relevantAppIds)){
			relevantApps = appProvider.getAppInfoList(orgId, relevantAppIds).getData();
			appFormConfigResp.setRelevantApps(com.polaris.lesscode.util.MapUtils.toMap(AppResp::getId, relevantApps));
		}

		// fields
		if (CollectionUtils.isNotEmpty(relevantAppIds)){
			relevantBizForms = summaryService.getBizForms(orgId, relevantAppIds);
			for (BizForm bform: relevantBizForms){
				for (Map.Entry<String, FieldParam> entry: FormFieldConstant.getCommonFields().entrySet()){
					bform.getFieldParams().putIfAbsent(entry.getKey(), entry.getValue());
				}
			}
			appFormConfigResp.setRelevantForms(com.polaris.lesscode.util.MapUtils.toMap(BizForm::getAppId, relevantBizForms));
		}
		Map<Long, BizForm> relevantBizFormMap = new HashMap<>();
		for (BizForm bf: relevantBizForms){
			relevantBizFormMap.put(bf.getAppId(), bf);
		}

		// 准备更新field中的关联字段
		fieldParams.forEach(fieldParam -> {
					Map<String, Object> props = fieldParam.getField().getProps();
					if (Objects.equals(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType(), fieldParam.getField().getType()) && MapUtils.isNotEmpty(props)){
						RelateTableProFieldConfig relateProConfig = JSON.parseObject(JSON.toJSONString(props.get(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())), RelateTableProFieldConfig.class);
						Long relateAppId = relateProConfig.getAppId();
						BizForm relateForm = relevantBizFormMap.get(relateAppId);
						if (relateForm != null){
							String currentCondFieldName = relateProConfig.getCurrentCond().getName();
//							String currentShowFieldName = relateProConfig.getCurrentShow().getName();
							String linkCondFieldName = relateProConfig.getLinkCond().getName();
							String linkShowFieldName = relateProConfig.getLinkShow().getName();
							relateProConfig.setCurrentCond(bizForm.getFieldParams().get(currentCondFieldName));
//							relateProConfig.setCurrentShow(bizForm.getFieldParams().get(currentShowFieldName));
							relateProConfig.setLinkCond(relateForm.getFieldParams().get(linkCondFieldName));
							relateProConfig.setLinkShow(relateForm.getFieldParams().get(linkShowFieldName));
							props.put(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType(), relateProConfig);
						}
					}else if (Objects.equals(FieldTypeEnums.QUOTE_TABLE.getFormFieldType(), fieldParam.getField().getType()) && MapUtils.isNotEmpty(props)){
						QuoteFieldConfig config = JSON.parseObject(JSON.toJSONString(props.get(FieldTypeEnums.QUOTE_TABLE.getFormFieldType())), QuoteFieldConfig.class);
						String[] subFields = config.getQuoteField().split("\\.");
						if (subFields.length > 1) {
							FieldParam topField = bizForm.getFieldParams().get(subFields[0]);
							if (topField != null && MapUtils.isNotEmpty(topField.getField().getProps())){
								Long thatAppId = 0L;
								if (Objects.equals(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType(), topField.getField().getType())){
									RelateTableProFieldConfig relateProConfig = JSON.parseObject(JSON.toJSONString(topField.getField().getProps().get(FieldTypeEnums.RELATION_TABLE_PRO.getFormFieldType())), RelateTableProFieldConfig.class);
									if (relateProConfig != null){
										thatAppId = relateProConfig.getAppId();
									}
								}else if(Objects.equals(FieldTypeEnums.RELATION_TABLE.getFormFieldType(), topField.getField().getType())){
									RelateTableFieldConfig referenceDataFieldConfig = JSON.parseObject(JSON.toJSONString(topField.getField().getProps().get(FieldTypeEnums.RELATION_TABLE.getFormFieldType())), RelateTableFieldConfig.class);
									if (referenceDataFieldConfig != null){
										thatAppId = referenceDataFieldConfig.getAppId();
									}
								}
								if (thatAppId != null){
									BizForm thatBizForm = relevantBizFormMap.get(thatAppId);
									if (thatBizForm != null){
										config.setQuoteFieldConfig(thatBizForm.getFieldParams().get(subFields[1]));
										props.put(FieldTypeEnums.QUOTE_TABLE.getFormFieldType(), config);
									}
								}
							}
						}
					}else if (Objects.equals(FieldTypeEnums.RELATION_TABLE.getFormFieldType(), fieldParam.getField().getType()) && MapUtils.isNotEmpty(props)){
						RelateTableFieldConfig referenceDataFieldConfig = JSON.parseObject(JSON.toJSONString(fieldParam.getField().getProps().get(FieldTypeEnums.RELATION_TABLE.getFormFieldType())), RelateTableFieldConfig.class);
						Long relateAppId = referenceDataFieldConfig.getAppId();
						BizForm relateForm = relevantBizFormMap.get(relateAppId);
						if (relateForm != null){
							referenceDataFieldConfig.setColumns(relateForm.getFieldList());
							props.put(FieldTypeEnums.RELATION_TABLE.getFormFieldType(), referenceDataFieldConfig);
						}
					}
				});

		if (! hiddenCommonField){
			Set<String> existField = fieldParams.stream().map(FieldParam::getName).collect(Collectors.toSet());
			for (FieldParam commonField: FormFieldConstant.getCommonFields().values()){
				if (! existField.contains(commonField.getName())){
					fieldParams.add(commonField);
				}
			}
		}
		appFormConfigResp.setFields(fieldParams);

		// columns
		List<Column> columns = fieldParams.stream().map(p -> {
			Column c = new Column();
			c.setKey(p.getName());
			c.setTitle(p.getLabel());
			c.setEnTitle(p.getEnLabel());
			c.setAliasTitle(p.getAliasLabel());
			c.setField(p.getField());
			c.setUnique(p.getUnique());
			c.setRules(p.getRules());
			c.setIsSys(p.getIsSys());
			c.setIsOrg(p.getIsOrg());
			c.setWritable(p.getWritable());
			c.setEditable(p.getEditable());
			if (!CollectionUtils.isEmpty(p.getFields())) {
				List<Children> children = p.getFields().stream().map(p2 -> {
					Children cr = new Children();
					cr.setKey(p2.getName());
					cr.setTitle(p2.getLabel());
					cr.setField(p2.getField());
					cr.setUnique(p2.getUnique());
					cr.setRules(p2.getRules());
					cr.setIsOrg(p2.getIsOrg());
					return cr;
				}).collect(Collectors.toList());
				c.setChildren(children);
			}
			return c;
		}).collect(Collectors.toList());
		appFormConfigResp.setColumns(columns);
		appFormConfigResp.setCustomConfig(bizForm.getConfig().getCustomConfig());
		appFormConfigResp.setAppId(bizForm.getAppId());
		return appFormConfigResp;
	}


}
