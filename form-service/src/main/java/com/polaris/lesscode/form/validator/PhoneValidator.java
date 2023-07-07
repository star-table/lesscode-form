package com.polaris.lesscode.form.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 手机号行文本校验格式: xxx xxxxxxx
 *
 * @Author Nico
 * @Date 2021/1/27 20:51
 **/
@ValidatorType(FieldTypeEnums.PHONE)
@Component
public class PhoneValidator extends AbstractValidator {

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private static final PhoneNumberOfflineGeocoder phoneNumberOfflineGeocoder = PhoneNumberOfflineGeocoder.getInstance();

    @Override
    public void validate(FieldParam fieldParam, Object value) throws ValidateError{
        requiredValidate(fieldParam, value);
        dataTypeValidate(fieldParam, value, String.class);

//        if (value != null && StringUtils.isNotBlank(value.toString())){
//            String phoneNum = String.valueOf(value);
//            boolean access = false;
//            if (phoneNum.indexOf(' ') > 0){
//                String language ="CN";
//                try {
//                    Phonenumber.PhoneNumber referencePhoneNumber = phoneUtil.parse(String.valueOf(value), language);
//                    access = phoneUtil.isValidNumber(referencePhoneNumber);
//                } catch (NumberParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (! access){
//                throw ValidateErrors.errorf("%s格式错误", fieldParam.getLabel());
//            }
//        }
    }

}
