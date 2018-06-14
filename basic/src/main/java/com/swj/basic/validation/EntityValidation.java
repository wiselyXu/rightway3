package com.swj.basic.validation;


import com.swj.basic.annotation.Length;
import com.swj.basic.annotation.Pattern;
import com.swj.basic.annotation.Range;
import com.swj.basic.annotation.Require;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 实体验证类
 * @author 刘海峰【liuhf@3vjia.com】
 * @since 2018/4/13-11:27
 **/
public class EntityValidation {

    private static Map<Class, Validator> annotationValidatorMap = new HashMap<>();

    static {
        annotationValidatorMap.put(Require.class, new RequireValidator());
        annotationValidatorMap.put(Range.class, new RangeValidator());
        annotationValidatorMap.put(Pattern.class, new PatternValidator());
        annotationValidatorMap.put(Length.class, new LengthValidator());
    }

    /**
     * 验证实体对象中否符合声明中的规范
     * @param entity 待验证的对象
     **/
    public static void validate(Object entity) throws IllegalAccessException {
        if (entity == null) return;

        List<ValidateResult> validateResultList = new ArrayList<>();

        // 校验父类
        Class superclass = entity.getClass().getSuperclass();
        while (superclass != null) {
            if (superclass.getName().equals("java.lang.Object")) {
                break;
            }
            validateResult(validateResultList, entity, superclass.getDeclaredFields());
            superclass = superclass.getSuperclass();
        }

        // 校验本身
        validateResult(validateResultList, entity, entity.getClass().getDeclaredFields());

        // 返回校验错误信息
        if (!validateResultList.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (int i = 0; i < validateResultList.size(); i++) {
                if (i > 0) errorMessage.append("; ");
                errorMessage.append(validateResultList.get(i).getErrorMessage());
            }
            throw new EntityValidationException(errorMessage.toString());
        }
    }

    private static void validateResult(List<ValidateResult> validateResultList, Object entity, Field[] fields) throws IllegalAccessException {
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotationValidatorMap.containsKey(annotation.annotationType())) {
                    Validator validator = annotationValidatorMap.get(annotation.annotationType());
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    ValidateResult validateResult = validator.validate(field.getName(), value, annotation);
                    if (!validateResult.isValid()) {
                        validateResultList.add(validateResult);
                    }
                }
            }
        }
    }

}
