package com.polaris.lesscode.form.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy;
import com.baomidou.mybatisplus.extension.api.R;
import com.polaris.lesscode.form.bo.ExcelHeaderConfig;
import com.polaris.lesscode.form.bo.ExcelReadObject;
import com.polaris.lesscode.form.bo.ExcelWriteObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-07-27 3:52 下午
 */
public class ExcelUtils {

    public static ExcelReadObject readObject(String file, List<ExcelHeaderConfig> excelHeaderConfigs){
        ExcelReadObject excelReadObject = new ExcelReadObject();
        excelReadObject.setHeaderConfigs(excelHeaderConfigs);
        List<LinkedHashMap<Integer, String>> listMap = EasyExcel.read(file, new HeadDataListener(excelReadObject)).sheet().doReadSync();
        if (listMap != null && listMap.size() > 0) {
            Map<Integer, ExcelHeaderConfig> index2Config = new LinkedHashMap<>();
            int index = 0;
            for (ExcelHeaderConfig config: excelHeaderConfigs){
                if (CollectionUtils.isEmpty(config.getChildExcelConfig())){
                    index2Config.put(index++, config);
                }else {
                    for (ExcelHeaderConfig childConfig: config.getChildExcelConfig()){
                        childConfig.setParentConfig(config);
                        index2Config.put(index++, childConfig);
                    }
                }
            }
            LinkedHashMap<Integer, String> header = listMap.get(0);
            excelReadObject.setHeader(header);
            listMap.remove(0);
            List<LinkedHashMap<String, Object>> dataList = new ArrayList<>();
            for (LinkedHashMap<Integer, String> rowData: listMap) {
                LinkedHashMap<String, Object> rowReturnData = new LinkedHashMap<>();
                for (Map.Entry<Integer, String> entry: rowData.entrySet()){
                    Integer dataIndex = entry.getKey();
                    String value = entry.getValue();
                    ExcelHeaderConfig config = index2Config.get(dataIndex);
                    if (config.getParentConfig() == null){
                        rowReturnData.put(config.getKey(), value);
                    }else {
                        ExcelHeaderConfig parentConfig = config.getParentConfig();
                        Object o = rowReturnData.get(parentConfig.getKey());
                        if (o == null){
                            if (config.getType().equals(0)){
                                o = new LinkedHashMap<>();
                                ((LinkedHashMap)o).put(config.getKey(), value);
                            }else {
                                o = new Object();
                            }
                            rowReturnData.put(parentConfig.getKey(), o);
                        }else {
                            if (config.getType().equals(0) && o instanceof Map){
                                Map<String, Object> map = (Map<String, Object>) o;
                                map.put(config.getKey(), value);
                                rowReturnData.put(parentConfig.getKey(), o);
                            }
                        }
                    }

                }
                dataList.add(rowReturnData);
            }
            excelReadObject.setData(dataList);
        }
        return excelReadObject;

    }

    public static void writeExcel(ExcelWriteObject excelWriteObject){
        String filePath = excelWriteObject.getFilePath();
        List<ExcelHeaderConfig> headerConfigs = excelWriteObject.getHeaderConfigs();
        List<List<String>> header = new ArrayList<>();
        
        List<WriteHandler> handlers = new ArrayList<>();
        
        boolean hasChildKey = false;
        for (ExcelHeaderConfig config: headerConfigs) {
        	if(! CollectionUtils.isEmpty(config.getChildExcelConfig())) {
        		hasChildKey = true;
        		break;
        	}
        }
        
        int columnIndex = 0;
        for (ExcelHeaderConfig config: headerConfigs){
            String title = config.getTitle();
            List<ExcelHeaderConfig> childExcelConfig = config.getChildExcelConfig();
            if (CollectionUtils.isEmpty(childExcelConfig)){
                List<String> item = new ArrayList<>();
                item.add(title);
                header.add(item);
                if(hasChildKey) {
                	handlers.add(new OnceAbsoluteMergeStrategy(0, 1, columnIndex, columnIndex));
                }
                columnIndex ++;
            }else {
            	int beforeIndex = columnIndex;
                for (ExcelHeaderConfig childConfig: childExcelConfig) {
                    List<String> item = new ArrayList<>();
                    item.add(title);
                    item.add(childConfig.getTitle());
                    header.add(item);
                    columnIndex ++;
                }
                handlers.add(new OnceAbsoluteMergeStrategy(0, 0, beforeIndex, columnIndex - 1));
            }
        }

        // 读取时传递的也是List<LinkedHashMap<String, Object>>结构 需要根据字段配置转成 List<List<Object>> 这种格式
        List<LinkedHashMap<String, Object>> originData = excelWriteObject.getData();

        List<List<Object>> data = new ArrayList<>();
        for(LinkedHashMap<String, Object> rowData: originData){
            List<Object> itemData = new ArrayList<>();
            for (Map.Entry<String, Object> entry: rowData.entrySet()){
                itemData.add(entry.getValue());
            }
            data.add(itemData);
        }

        File createDir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
        if(! createDir.exists()){
            createDir.mkdirs();
        }
        
        String sheetName = excelWriteObject.getSheetName();
        ExcelWriterSheetBuilder excelBuilder =  EasyExcel
	        .write(filePath)
	        .sheet(sheetName);
        if(!CollectionUtils.isEmpty(handlers)) {
        	handlers.forEach(excelBuilder::registerWriteHandler);
        }
        
        excelBuilder
        .automaticMergeHead(false)
        .head(header)
        .doWrite(data);
    }

    public static List<ReadSheet> sheets(InputStream inputStream) {
        List<ReadSheet> sheets = new ArrayList<>();
        Workbook wb = null;
        try {
            try{
                wb = new XSSFWorkbook(inputStream);
            }catch(Exception e){
                wb = new HSSFWorkbook(inputStream);
            }
            for (int i = 0; i < wb.getNumberOfSheets(); i ++){
                Sheet sheet = wb.getSheetAt(i);
                ReadSheet readSheet = new ReadSheet();
                readSheet.setSheetNo(i);
                readSheet.setSheetName(sheet.getSheetName());
                sheets.add(readSheet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sheets;
    }

}
