package com.polaris.lesscode.form.internal.sula;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 约束
 *
 * @author Nico
 * @date 2021/2/3 10:13
 */
@Data
public class Constraint {

    /**
     * 删除约束
     **/
    private List<DeleteConstraint> deleteConstraints;

}
