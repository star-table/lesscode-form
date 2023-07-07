package com.polaris.lesscode.form.util;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.polaris.lesscode.form.constant.CommonField;
import com.polaris.lesscode.form.constant.FormFieldConstant;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.form.internal.util.HeaderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Conditions;
import com.polaris.lesscode.dc.internal.dsl.Order;
import com.polaris.lesscode.dc.internal.dsl.SqlUtil;

public class DslUtil {

	public static String wrapperJsonColumns(String column) {
		if(!"id".equalsIgnoreCase(column) && !"count(1)".equalsIgnoreCase(column) && !"sort".equalsIgnoreCase(column)){
			return SqlUtil.wrapperJsonColumn(column);
		}
		return column;
	}

	public static String wrapperJsonColumns(String column, String fieldType) {
		if(!"id".equalsIgnoreCase(column) && !"count(1)".equalsIgnoreCase(column) && !"sort".equalsIgnoreCase(column)){
			String wrappedJsonColumn = SqlUtil.wrapperJsonColumn(column);
			if (fieldType.equals(FieldTypeEnums.NUMBER.getDataType())
					|| fieldType.equals(FieldTypeEnums.AMOUNT.getDataType())) {
				return "(" + wrappedJsonColumn + ")::float";
			}
			return wrappedJsonColumn;
		}
		return column;
	}

//	private static boolean isJsonColumn(String column){
//		return ! column.equals("id") &&
//				! column.equals("sort") &&
//				! column.equals("");
//	}

	public static List<String> getReqColumns(List<String> columns){
		if(CollectionUtils.isNotEmpty(columns)) {
			return columns.stream().map(c->{
				return wrapperJsonColumns(c);
			}).collect(Collectors.toList());
		}
		return columns;
	}

	public static List<Order> convertOrders(List<Order> orders){
		if(CollectionUtils.isNotEmpty(orders)) {
			orders.stream().forEach(order -> {
				order.setColumn(wrapperJsonColumns(order.getColumn()));
			});	
		}
		return orders;
	}

	public static List<Order> resetReqOrders(List<Order> orders, Map<String, FieldParam> fieldParams, String alias){
		if(CollectionUtils.isNotEmpty(orders)) {
			orders.forEach(order -> {
				// 临时兼容
				if (Objects.equals("sort", order.getColumn())){
					order.setColumn(SqlUtil.wrapperAliasJsonColumn(alias,"order"));
					order.setIgnoreNullsOrder(true);
				} else {
					FieldParam fieldParam = fieldParams.get(order.getColumn());
					if (fieldParam != null && Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.SELECT.getFormFieldType())){
						List<String> options = HeaderUtil.parseSelectOptionsList(fieldParam);
						// 产品说选项最后一个元素排在最上边
//					Collections.reverse(options);
						order.setArgs(new ArrayList<>(options));
						String orderColumnBuilder = "array_position(array[" + StringUtils.join(options.stream().map(o -> "?").collect(Collectors.toList()), ",") +
								"]::text[]," + SqlUtil.wrapperAliasColumn(alias,SqlUtil.wrapperJsonTextColumn(order.getColumn())) + ")";
						order.setColumn(orderColumnBuilder);
					}else if (! Objects.equals(FormFieldConstant.ID, order.getColumn())){
						if (fieldParam != null){
							FieldTypeEnums fieldTypeEnums = FieldTypeEnums.formatByFieldType(fieldParam.getField().getType());
							if (fieldTypeEnums == FieldTypeEnums.SINGLE_TEXT || fieldTypeEnums == FieldTypeEnums.MUL_TEXT || fieldTypeEnums == FieldTypeEnums.RICH_TEXT){
								order.setColumn("convert_to(" + SqlUtil.wrapperAliasColumn(alias,SqlUtil.wrapperJsonTextColumn(order.getColumn())) + ",'GB18030')");
								return;
							}
						}
						order.setColumn(SqlUtil.wrapperAliasJsonColumn(alias,order.getColumn()));
					} else {
						order.setColumn(SqlUtil.wrapperAliasColumn(alias,order.getColumn()));
					}
				}
			});
		}
		return orders;
	}

	public static Condition resetReqCondition(Condition condition){
		if(condition != null){
			if(Conditions.RAW_SQL.equals(condition.getType())) {
				return condition;
			}

			if(! StringUtils.isBlank(condition.getColumn()) && ! "id".equals(condition.getColumn())
					&& ! "parent_id".equals(condition.getColumn()) && ! condition.getColumn().contains("::jsonb")){
				condition.setColumn(wrapperJsonColumns(condition.getColumn()));
			}
			if ("id".equals(condition.getColumn()) || "parent_id".equals(condition.getColumn())){
				if (Objects.nonNull(condition.getValue())){
					condition.setValue(Long.valueOf(String.valueOf(condition.getValue())));
				}else if (ArrayUtils.isNotEmpty(condition.getValues())){
					Object[] newValues = new Object[condition.getValues().length];
					for (int i = 0; i < condition.getValues().length; i ++){
						newValues[i] = Long.valueOf(String.valueOf(condition.getValues()[i]));
					}
					condition.setValues(newValues);
				}
			}
			if(! ArrayUtils.isEmpty(condition.getConds())){
				for (Condition cond : condition.getConds()) {
					resetReqCondition(cond);
				}
			}
			if (condition.getValue() instanceof String){
				String value = (String) condition.getValue();
				if (value.startsWith("U_") && value.endsWith("_member")){
					value = value.replaceAll("_member", "");
				}
				condition.setValue(value);
			}
			if (ArrayUtils.isNotEmpty(condition.getValues())){
				for (int i = 0; i < condition.getValues().length; i ++){
					if (condition.getValues()[i] instanceof String){
						String value = (String) condition.getValues()[i];
						if (value.startsWith("U_") && value.endsWith("_member")){
							value = value.replaceAll("_member", "");
						}
						condition.getValues()[i] = value;
					}
				}
			}
		}
		return condition;
	}

	public static List<Condition> resetReqCondition(List<Condition> conds){
		if(CollectionUtils.isNotEmpty(conds)){
			for(Condition c: conds){
				resetReqCondition(c);
			}
		}
		return conds;
	}

	public static Condition getWrapperCondition(Condition condition){
		if(condition != null){
			condition = Conditions.and(condition, Conditions.equal("delFlag", CommonConsts.NO_DELETE));
		}else{
			condition = Conditions.equal("delFlag", CommonConsts.NO_DELETE);
		}
		condition = resetReqCondition(condition);
		return condition;
	}

	public static List<String> getReqGroups(List<String> groups){
		if(CollectionUtils.isNotEmpty(groups)) {
			return groups.stream().map(DslUtil::wrapperJsonColumns).collect(Collectors.toList());
		}
		return groups;
	}

}
