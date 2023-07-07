package com.polaris.lesscode.form.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.polaris.lesscode.app.internal.feign.AppProvider;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.dc.internal.feign.DataCenterProvider;
import com.polaris.lesscode.dc.internal.model.AddStorageValue;
import com.polaris.lesscode.dc.internal.model.StorageValue;
import com.polaris.lesscode.dc.internal.req.AddValueReq;
import com.polaris.lesscode.dc.internal.req.UpdateValueReq;
import com.polaris.lesscode.dc.internal.req.ValueFilterReq;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.entity.AppForm;
import com.polaris.lesscode.form.internal.enums.MemoType;
import com.polaris.lesscode.form.internal.enums.YesOrNoEnum;
import com.polaris.lesscode.form.mapper.AppFormMapper;
import com.polaris.lesscode.form.req.AppMemoValueAddReq;
import com.polaris.lesscode.form.req.AppMemoValueDeleteReq;
import com.polaris.lesscode.form.req.AppMemoValueListReq;
import com.polaris.lesscode.form.req.AppMemoValueUpdateReq;
import com.polaris.lesscode.form.resp.AppFormResp;
import com.polaris.lesscode.form.resp.AppMemoResp;
import com.polaris.lesscode.form.util.DslUtil;
import com.polaris.lesscode.form.util.PdfUtil;
import com.polaris.lesscode.form.vo.ResultCode;
import com.polaris.lesscode.permission.internal.model.resp.FromPerOptAuthVO;
import com.polaris.lesscode.util.DataSourceUtil;
import com.polaris.lesscode.util.DateTimeFormatterUtils;
import com.polaris.lesscode.util.DateTimeUtils;
import com.polaris.lesscode.vo.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AppMemoService{

    @Autowired
    private AppProvider appProvider;

    @Autowired
    private AppFormMapper appFormMapper;

    @Autowired
    private AppFormService appFormService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DataCenterProvider dataCenterProvider;

    @Value("${img.rootPath}")
    private String imgRootPath;

    @Value("${img.localDomain}")
    private String imgLocalDomain;

    @Value("${pdf.rootPath}")
    private String pdfRootPath;

    public static final String datePattern = "\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}\\:\\d{2}";

    private static final int MAX_SIZE = 500;

    public Map uploadImage(Long orgId, Long appId, MultipartFile image){
        AppResp app = appProvider.getAppInfo(orgId, appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        Map<String,Object> map = new HashMap<String,Object>();
        if(image !=null){
            try{
                String fileName = UUID.randomUUID() +"."+image.getContentType().substring(
                        image.getContentType().lastIndexOf("/")+1);
                String filePath = imgRootPath + fileName;
                String url = imgLocalDomain + fileName;
                map.put("filePath",filePath);
                map.put("url",url);
                map.put("filename",image.getOriginalFilename());
                map.put("contentType",image.getContentType());
                map.put("size",image.getSize());
                File createDir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if(! createDir.exists()){
                    createDir.mkdirs();
                }
                File saveFile = new File(filePath);
                image.transferTo(saveFile);
            }catch (Exception e){
                throw new BusinessException(ResultCode.IMAGE_UPLOAD_FAIL);
            }
        }
        return map;
    }

    /*public Map downloadWord(Long appId, Long memoId){
        Map<String, Object> resultMap = new HashMap<>();
        AppResp app = appProvider.getAppInfo(appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        AppFormResp appFormResp = appFormService.get(appId);
        if(appFormResp == null){
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        Map<String, Object> memoMap = dataCenterProvider.get(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), SqlUtil.wrapperTableName(app.getOrgId(), appFormResp.getId()), memoId).getData();
        String url = "";
        String filePath = "";
        if(! MapUtils.isEmpty(memoMap)){
            AppMemoResp appMemoResp = JSONObject.parseObject(JSON.toJSONString(memoMap), AppMemoResp.class);
            //Word07Writer writer = new Word07Writer();
            String fileName = "备忘录_"+memoId+".docx";
            filePath = wordRootPath + fileName;
            url = wordLocalDomain + filePath;
            File createDir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if(! createDir.exists()){
                createDir.mkdirs();
            }
            *//*File createDir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if(! createDir.exists()){
                createDir.mkdirs();
            }*//*
            //（标题）
            *//*writer.addText(ParagraphAlignment.CENTER, new Font("方正小标宋简体", Font.PLAIN, 22), appMemoResp.getTitle());
            //（正文）
            writer.addText(new Font("宋体", Font.PLAIN, 22), appMemoResp.getContent());
            writer.flush(FileUtil.file(filePath));
            writer.close();*//*

            //配置
            Configure config = Configure.newBuilder().build();
            config.customPolicy("contentHtml", WordUtil.createHtmlRenderPolicy());

            //创建word模板对象
            Map<String, Object> fieldMap = new HashMap<String, Object>();
            fieldMap.put("title", appMemoResp.getTitle());
            fieldMap.put("contentHtml", appMemoResp.getContent());
            XWPFTemplate template = XWPFTemplate.compile(WordUtil.getResourceInputStream("/memo_template.docx"), config).render(fieldMap);

            try{
                template.writeToFile(filePath);
                template.close();
            }catch (Exception e){
                throw new BusinessException(ResultCode.MEMO_WORD_DOWNLOAD_FAIL);
            }
        }
        resultMap.put("url", url);
        return resultMap;
    }*/

    public void downloadPdf(Long orgId, Long appId, Long memoId, HttpServletResponse response) throws Exception {
        AppResp app = appProvider.getAppInfo(orgId, appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        AppFormResp appFormResp = appFormService.get(appId);
        if(appFormResp == null){
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        List<Map<String, Object>> datas = dataCenterProvider.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), Query.select().from(new Table(SqlUtil.wrapperTableName(app.getOrgId(), appFormResp.getId()))).where(Conditions.equal("id", memoId))).getData();
        Map<String, Object> memoMap = CollectionUtils.isNotEmpty(datas) ? datas.get(0) : null;
        if(! MapUtils.isEmpty(memoMap)){
            AppMemoResp appMemoResp = JSONObject.parseObject(JSON.toJSONString(memoMap), AppMemoResp.class);
            String fileName = appMemoResp.getTitle()+"_"+memoId+".pdf";
            String filePath = pdfRootPath + fileName;
            // url = pdfLocalDomain + fileName;
            PdfUtil.html2Pdf(PdfUtil.content2Html(appMemoResp.getTitle(), appMemoResp.getContent()), filePath);
            File file = new File(filePath);
            if(file.exists()){
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                // 实现文件下载
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e.getStackTrace());
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e.getStackTrace());
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e.getStackTrace());
                        }
                    }

                }
            }
        }
    }

    public Page<AppMemoResp> filter(Long orgId, Long appId, Long userId, AppMemoValueListReq req) {
        AppResp app = appProvider.getAppInfo(orgId, appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        AppFormResp appFormResp = appFormService.get(appId);
        if(appFormResp == null){
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        ValueFilterReq filterReq = new ValueFilterReq();
        int page = req.getPage();
        int size = req.getSize();
        if (page <= 0) page = 1;
        if (size <= 0 || size > MAX_SIZE) size = MAX_SIZE;
        int offset = (page - 1) * size;
        filterReq.setLimit(size);
        filterReq.setColumns(DslUtil.getReqColumns(req.getColumns()));
        filterReq.setGroups(DslUtil.getReqGroups(req.getGroups()));
        filterReq.setOffset(offset);

        if(req.getAppoint()){
            Condition condition = req.getCondition();
            Condition[] conds = condition.getConds();
            List<Condition> oldList = Arrays.asList(conds);
            List<Condition> newList = new ArrayList<>();
            Condition subCondition = new Condition();
            subCondition.setType(Conditions.EQUAL);
            subCondition.setColumn("creator");
            subCondition.setValue(String.valueOf(userId));
            newList.add(subCondition);
            List<Condition> tempList = new ArrayList<>();
            tempList.addAll(oldList);
            tempList.addAll(newList);
            Condition[] allConds = tempList.toArray(new Condition[tempList.size()]);
            condition.setConds(allConds);
            filterReq.setCondition(DslUtil.getWrapperCondition(condition));
        }else{
            Condition condition = new Condition();
            condition.setType(Conditions.AND);
            Condition[] conds = new Condition[1];
            Condition subCondition = new Condition();
            subCondition.setType(Conditions.EQUAL);
            subCondition.setColumn("creator");
            subCondition.setValue(String.valueOf(userId));
            conds[0] = subCondition;
            condition.setConds(conds);
            filterReq.setCondition(DslUtil.getWrapperCondition(condition));
        }

        List<Order> orders = new ArrayList<>();

        Order orderByIsCompletionDesc = new Order();
        orderByIsCompletionDesc.setColumn("isCompletion");
        orderByIsCompletionDesc.setAsc(false);
        orders.add(orderByIsCompletionDesc);

        Order orderByTypeDesc = new Order();
        orderByTypeDesc.setColumn("type");
        orderByTypeDesc.setAsc(false);
        orders.add(orderByTypeDesc);

        Order orderByIdDesc = new Order();
        orderByIdDesc.setColumn("id");
        orderByIdDesc.setAsc(false);
        orders.add(orderByIdDesc);


        Query query = new Query();
        query.setFrom(Arrays.asList(new Table(SqlUtil.wrapperTableName(orgId, appFormResp.getId()))));
        if(CollectionUtils.isNotEmpty(filterReq.getColumns())){
            query.setColumns(filterReq.getColumns());
        }
        if(filterReq.getCondition() != null){
            query.setCondition(filterReq.getCondition());
        }
        if(CollectionUtils.isNotEmpty(filterReq.getGroups())){
            query.group(filterReq.getGroups());
        }
        if(CollectionUtils.isNotEmpty(filterReq.getOrders())){
            query.orders(filterReq.getOrders());
        }
        if(filterReq.getOffset() != null){
            query.offset(filterReq.getOffset());
        }
        if(filterReq.getLimit() != null){
            query.limit(filterReq.getLimit());
        }
        log.info("---AppMemoService---query-->{}", JSON.toJSONString(query));

        Page<Map<String, Object>> pages = dataCenterProvider.page(DataSourceUtil.getDsId(),
                DataSourceUtil.getDbId(), query).getData();
        List<AppMemoResp> resps = new ArrayList<>();
        if(! CollectionUtils.isEmpty(pages.getList())){
            for (Map<String, Object> memoMap : pages.getList()) {
                AppMemoResp appMemoResp = JSONObject.parseObject(JSON.toJSONString(memoMap), AppMemoResp.class);
                resps.add(appMemoResp);
            }
        }
        Page<AppMemoResp> respPages = new Page<>(pages.getTotal(), resps);
        return respPages;
    }

    public boolean add(Long orgId, Long appId, Long userId, AppMemoValueAddReq req) {
        if(MemoType.formatOrNull(req.getType()) == null){
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        checkDateTime(req.getAlarmTime());
        AppResp app = appProvider.getAppInfo(orgId, appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        AppForm appForm = appFormMapper.getByAppId(appId);
        if (appForm == null) {
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        FromPerOptAuthVO optAuth = permissionService.opAuth(orgId, appId, RequestContext.currentUserId());
        if (optAuth == null || !optAuth.hasCreate()) {
            throw new BusinessException(ResultCode.FORM_OP_NO_CREATE);
        }

        Map<String, Object> memoMap = JSONObject.parseObject(JSON.toJSONString(req));
        memoMap.put("isCompletion", YesOrNoEnum.NO.getCode());
        memoMap.put("completionTime", "");
        String tableName = SqlUtil.wrapperTableName(orgId, appForm.getId());
        String dtNow = DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime());
        resetMemoMap(userId, userId, memoMap, CommonConsts.NO_DELETE, dtNow, dtNow);

        Executor insert = Executor.insert(new Table(tableName)).columns("id", "data").values(Values.value(IdWorker.getId(), memoMap));

        int affects = dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), insert).getData();
        if(affects == 0){
            throw new BusinessException(ResultCode.MEMO_VALUE_ADD_FAIL);
        }
        return true;
    }

    public AppMemoResp update(Long orgId, Long appId, Long userId, AppMemoValueUpdateReq req) {
        if(MemoType.formatOrNull(req.getType()) == null){
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        checkDateTime(req.getAlarmTime(), req.getCompletionTime());
        AppResp app = appProvider.getAppInfo(orgId, appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }
        FromPerOptAuthVO optAuth = permissionService.opAuth(orgId, appId, RequestContext.currentUserId());
        if (optAuth == null || !optAuth.hasUpdate()) {
            throw new BusinessException(ResultCode.FORM_OP_NO_UPDATE.getCode(),
                    ResultCode.FORM_OP_NO_UPDATE.getMessage());
        }
        AppForm appForm = appFormMapper.getByAppId(appId);
        if (appForm == null) {
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        if(req.getIsCompletion() != null && YesOrNoEnum.formatOrNull(req.getIsCompletion()) == null){
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if(YesOrNoEnum.NO.getCode().equals(req.getIsCompletion())){
            req.setCompletionTime("");
        }

        Map<String, Object> datas = new HashMap<>();
        Map<String, Object> memoMap = JSONObject.parseObject(JSON.toJSONString(req));
        String appMemoId = String.valueOf(memoMap.get("id"));
        if(StringUtils.isBlank(appMemoId)){
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        memoMap.keySet().removeIf(key -> key.equals("id"));
        String rowDataStr = JSON.toJSONString(resetMemoMap(null, userId, memoMap, null, null, DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime())));
        datas.put(appMemoId, rowDataStr);

        Executor executor = Executor.update(new Table(SqlUtil.wrapperTableName(orgId, appForm.getId())))
                .set(Sets.setJsonB("data", memoMap))
                .where(Conditions.equal("id", appMemoId));

        boolean suc = dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executor).getData() > 0;
        if(suc){
            AppMemoResp memoResp = JSONObject.parseObject(JSON.toJSONString(memoMap), AppMemoResp.class);
            return memoResp;
        }
        throw new BusinessException(ResultCode.MEMO_VALUE_UPDATE_FAIL);
    }

    private void checkDateTime(String... dateTimes){
        for (String dateTime : dateTimes) {
            if(! StringUtils.isBlank(dateTime)){
                Pattern r = Pattern.compile(datePattern);
                Matcher m = r.matcher(dateTime);
                if(! m.matches()){
                    throw new BusinessException(ResultCode.DATE_FORMAT_FAIL);
                }
            }
        }
    }

    // 逻辑删除
    public Integer delete(Long orgId, Long appId, Long userId, AppMemoValueDeleteReq req) {
        if (CollectionUtils.isEmpty(req.getAppMemoValueIds())) {
            throw new BusinessException(ResultCode.FORM_VALUE_NOT_SELECTED);
        }
        AppResp app = appProvider.getAppInfo(orgId, appId).getData();
        if (app == null) {
            throw new BusinessException(ResultCode.APP_NOT_EXIST);
        }

        FromPerOptAuthVO optAuth = permissionService.opAuth(orgId, appId, RequestContext.currentUserId());
        if (optAuth == null || !optAuth.hasDelete()) {
            throw new BusinessException(ResultCode.FORM_OP_NO_DELETE.getCode(),
                    ResultCode.FORM_OP_NO_DELETE.getMessage());
        }

        AppForm appForm = appFormMapper.getByAppId(appId);
        if (appForm == null) {
            throw new BusinessException(ResultCode.APP_FORM_NOT_EXIST);
        }
        List<StorageValue> storageValues = new ArrayList<>();
        String data = JSON.toJSONString(resetMemoMap(null, userId, new HashMap<>(), CommonConsts.DELETED, null, DateTimeFormatterUtils.getDateTimeString(DateTimeUtils.getCurrentDateTime())));;
        for (Long appMemoValueId : req.getAppMemoValueIds()) {
            StorageValue storage = new StorageValue();
            storage.setId(appMemoValueId);
            storage.setData(data);
            storageValues.add(storage);
        }
        UpdateValueReq updateValue = new UpdateValueReq();
        updateValue.setValues(storageValues);

        Executor executor = Executor.update(new Table(SqlUtil.wrapperTableName(orgId, appForm.getId())))
                .set(Sets.set("delFlag", 1))
                .where(Conditions.in("id", req.getAppMemoValueIds()));

        Integer result = dataCenterProvider.execute(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(), executor).getData();
        return result;

    }

    private Map<String, Object> resetMemoMap(Long creator, Long updator, Map<String, Object> dataMap, Integer delFlag, String createTime, String updateTime){
        if(creator != null){
            dataMap.put("creator", creator);
        }
        if(updator != null){
            dataMap.put("updator", updator);
        }
        if(! StringUtils.isBlank(createTime)){
            dataMap.put("createTime", createTime);
        }
        if(! StringUtils.isBlank(updateTime)){
            dataMap.put("updateTime", updateTime);
        }
        if(delFlag != null){
            dataMap.put("delFlag", delFlag);
        }
        return dataMap;
    }

}
