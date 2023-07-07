package com.polaris.lesscode.form.config;

import com.polaris.lesscode.form.validator.ValidateError;
import com.polaris.lesscode.form.validator.ValidatorType;
import com.polaris.lesscode.form.validator.AbstractValidator;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Liu.B.J
 * @date: 2021/1/27 11:51
 * @description:
 */
@Component
public class ValidatorContext implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private static final Map<FieldTypeEnums, AbstractValidator> VALIDATORS = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ValidatorType.class);
        beans.forEach((beanName, bean) -> {
            if (bean instanceof AbstractValidator) {
                AbstractValidator check = (AbstractValidator) bean;
                ValidatorType checkType = check.getClass().getAnnotation(ValidatorType.class);
                FieldTypeEnums type = checkType.value();
                VALIDATORS.put(type, check);
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static void validate(FieldTypeEnums type, FieldParam fieldParam, Object value) throws ValidateError{
        AbstractValidator validator = VALIDATORS.get(type);
        if (validator != null) {
            validator.validate(fieldParam, value);
        }
    }

}
