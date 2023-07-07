package com.polaris.lesscode.form.validator;

import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Map;

@ValidatorType(FieldTypeEnums.BA_RELATING)
@Component
public class BaRelatingValidator extends RelatingValidator {
}