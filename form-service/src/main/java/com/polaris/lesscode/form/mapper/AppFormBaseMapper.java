package com.polaris.lesscode.form.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.form.entity.AppFormBase;

public interface AppFormBaseMapper extends BaseMapper<AppFormBase> {

    default AppFormBase get(Long orgId){
        return selectOne(new LambdaQueryWrapper<AppFormBase>().eq(AppFormBase::getOrgId, orgId).eq(AppFormBase::getDelFlag, CommonConsts.FALSE).last("limit 1"));
    }
}
