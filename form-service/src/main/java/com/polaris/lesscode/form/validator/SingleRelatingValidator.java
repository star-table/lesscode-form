package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import org.springframework.stereotype.Component;

@ValidatorType(FieldTypeEnums.SINGLE_RELATING)
@Component
public class SingleRelatingValidator extends RelatingValidator {
}