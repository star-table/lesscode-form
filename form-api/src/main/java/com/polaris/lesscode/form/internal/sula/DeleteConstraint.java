package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 删除约束
 *
 * @author Nico
 * @date 2021/2/3 13:49
 */
@Data
public class DeleteConstraint {

    /**
     * 类型，1：限制删除，2：级联删除
     **/
    private int type;

    /**
     * 表名
     **/
    private String table;

    /**
     * 删除条件，满足条件
     **/
    private Condition condition;

}
