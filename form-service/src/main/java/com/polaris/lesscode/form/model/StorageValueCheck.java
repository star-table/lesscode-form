package com.polaris.lesscode.form.model;

import lombok.Data;

import java.util.Map;

/**
 * @Author: Liu.B.J
 * @Data: 2020/9/2 10:07
 * @Modified:
 */
@Data
public class StorageValueCheck {

    private String title;

    private String description;

    private String type;

    private Map properties;

    public StorageValueCheck(){
        this.title = "Match Template";
        this.description = "This is a schema that matches storageValue-jsons.";
        this.type = "object";
    }
}
