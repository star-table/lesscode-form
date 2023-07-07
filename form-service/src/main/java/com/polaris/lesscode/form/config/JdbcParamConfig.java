package com.polaris.lesscode.form.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-06-11 5:06 下午
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource.click")
@Data
public class JdbcParamConfig {

    private String driverClassName;
    private String url;
    private Integer initialSize ;
    private Integer maxActive ;
    private Integer minIdle ;
    private Integer maxWait ;
    private String username;
    private String password;

}
