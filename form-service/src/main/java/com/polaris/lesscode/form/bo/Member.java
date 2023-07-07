package com.polaris.lesscode.form.bo;

import lombok.Data;

/**
 * 成员
 *
 * @author Nico
 * @date 2021/2/20 18:53
 */
@Data
public class Member {

    private String id;

    private String name;

    private String avatar;

    private String type;

    private Integer status;

    private Integer isDelete;

    public Member(Long id, String name, String avatar, String type, Integer status, Integer isDelete) {
        this.id = id.toString();
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.status = status;
        this.isDelete = isDelete;
    }

    public Member() {
    }

}
