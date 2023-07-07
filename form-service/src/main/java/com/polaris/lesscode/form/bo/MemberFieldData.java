package com.polaris.lesscode.form.bo;

import lombok.Data;

/**
 * @author roamer
 * @version v1.0
 * @date 2021/2/1 11:06
 */
@Data
public class MemberFieldData {

    /**
     * id
     */
    private String id;

    /**
     * 实际指向的ID
     */
    private Long realId;

    /**
     * 类型
     * <ul>
     *     <li>U_</li>
     *     <li>R_</li>
     *     <li>D_</li>
     * </ul>
     */
    private String type;

}
