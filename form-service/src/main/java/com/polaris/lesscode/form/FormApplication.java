package com.polaris.lesscode.form;

import com.polaris.lesscode.form.util.MinioUtil;
import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.springframework.core.env.Environment;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableFeignClients(basePackages = {"com.polaris.lesscode.app.internal.feign",
		"com.polaris.lesscode.permission.internal.feign",
		"com.polaris.lesscode.dc.internal.feign",
        "com.polaris.lesscode.workflow.internal.feign",
        "com.polaris.lesscode.msgsvc.internal.feign",
        "com.polaris.lesscode.gotable.internal.feign",
        "com.polaris.lesscode.uc.internal.feign"})
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class FormApplication {
    private static ConfigurableApplicationContext application;
    public static void main(String[] args) {
        Sentry.getStoredClient().setEnvironment(System.getenv("SERVER_ENVIROMENT"));
        application =  SpringApplication.run(FormApplication.class, args);

        Environment env = application.getEnvironment();

        MinioUtil.setEndPoint(env.getProperty("minio.endPoint"));
        MinioUtil.setKey(env.getProperty("minio.key"));
        MinioUtil.setSecretKey(env.getProperty("minio.secretKey"));
    }
}
