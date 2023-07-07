package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.springframework.stereotype.Component;

/**
 * 邮件校验
 *
 * @author Nico
 * @date 2021/1/27 17:12
 */
@ValidatorType(FieldTypeEnums.EMAIL)
@Component
public class EmailValidator extends AbstractValidator {
    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        dataTypeValidate(fieldParam, value, String.class);

//        if (value != null){
//            String mail = String.valueOf(value);
//            if (! isMail(mail)){
//                throw ValidateErrors.errorf("%s不符合邮箱格式: %s", fieldParam.getLabel(), mail);
//            }
//        }
    }

    public static boolean isMail(String mail){
        int atIndex = mail.indexOf('@');
        int dotIndex = mail.indexOf('.');
        return atIndex > 0 && dotIndex - atIndex > 1 && dotIndex < mail.length() - 1;
    }

}
