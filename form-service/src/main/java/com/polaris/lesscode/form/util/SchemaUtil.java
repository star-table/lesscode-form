package com.polaris.lesscode.form.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.vo.ResultCode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @Author: Liu.B.J
 * @Data: 2020/9/2 11:40
 * @Modified:
 */
public class SchemaUtil {

    public static JsonNode strToJsonNode(String jsonStr) {
        JsonNode jsonNode = null;
        try {
            jsonNode = JsonLoader.fromString(jsonStr);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.JSONNODE_FROM_ERROR.getCode(), ResultCode.JSONNODE_FROM_ERROR.getMessage());
        }
        return jsonNode;
    }

    public static boolean getProcessingReport(JsonNode jsonNode, JsonNode schemaNode) {
        //fge验证json数据是否符合json schema约束规则
        ProcessingReport report = JsonSchemaFactory.byDefault().getValidator().validateUnchecked(schemaNode, jsonNode);
        if (report.isSuccess()) {
            return true;
        } else {
            Iterator<ProcessingMessage> it = report.iterator();
            StringBuilder ms = new StringBuilder();
            ms.append("json格式错误: ");
            while (it.hasNext()) {
                ProcessingMessage pm = it.next();
                if (!LogLevel.WARNING.equals(pm.getLogLevel())) {
                    ms.append(pm);
                }
            }
            return false;
        }
    }

}
