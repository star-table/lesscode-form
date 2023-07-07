package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.form.internal.enums.QuoteType;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 引用表类型字段
 *
 * @author roamer
 * @version v1.0
 * @date 2021/2/3 16:03
 */
@Data
public class QuoteFieldConfig {

    private static final String DEFAULT_TYPE = QuoteType.VALUES.getCode();

    /**
     * 引用字段列表
     **/
    private String quoteField;

    /**
     * 引用方式
     */
    private String quoteType;

    /**
     * jsonPath
     **/
    private String jsonPath;

    /**
     * 引用的字段类型
     **/
    private FieldParam quoteFieldConfig;

    public QuoteFieldConfig() {
        quoteType = DEFAULT_TYPE;
    }

    public String getQuoteType() {
        return Objects.isNull(quoteType) ? DEFAULT_TYPE : quoteType;
    }


}
