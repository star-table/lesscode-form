package com.polaris.lesscode.form.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.form.entity.AppForm;

public interface AppFormMapper extends BaseMapper<AppForm>{

	default List<AppForm> selectList(Long appId, Integer status) {
		return this.selectList(new LambdaQueryWrapper<AppForm>().eq(AppForm :: getAppId, appId).eq(AppForm :: getStatus, status).eq(AppForm :: getDelFlag, CommonConsts.NO_DELETE));
    }
	
	default AppForm getByAppId(Long appId) {
		return this.selectOne(new LambdaQueryWrapper<AppForm>().eq(AppForm :: getAppId, appId).eq(AppForm :: getDelFlag, CommonConsts.NO_DELETE).last("limit 1"));
	}

	default AppForm getByAppId(Long appId, Long orgId) {
		return this.selectOne(new LambdaQueryWrapper<AppForm>().eq(AppForm :: getAppId, appId).eq(AppForm :: getOrgId, orgId).eq(AppForm :: getDelFlag, CommonConsts.NO_DELETE).last("limit 1"));
	}
}
