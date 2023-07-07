package com.polaris.lesscode.form.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.entity.SysRegion;
import com.polaris.lesscode.form.mapper.SysRegionMapper;
import com.polaris.lesscode.form.req.SysRegionListReq;
import com.polaris.lesscode.form.resp.SysRegionResp;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Liu.B.J
 * @date: 2021/1/21 11:32
 * @description:
 */
@Slf4j
@Service
public class RegionService {

    @Autowired
    private AppProvider appProvider;

    @Autowired
    private SysRegionMapper sysRegionMapper;

    public List<SysRegionResp> getRegionList(Long orgId, Long appId, SysRegionListReq req){
        AppResp appResp = appProvider.getAppInfo(orgId, appId).getData();
        if (appResp == null) {
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        LambdaQueryWrapper<SysRegion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRegion :: getLevel, req.getLevel());
        if(req.getParentId() != null){
            queryWrapper.eq(SysRegion :: getParentId, req.getParentId());
        }
        return ConvertUtil.convertList(sysRegionMapper.selectList(queryWrapper), SysRegionResp.class);
    }

}
