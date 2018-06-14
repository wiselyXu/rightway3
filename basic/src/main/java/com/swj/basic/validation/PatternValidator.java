package com.swj.basic.validation;

import com.swj.basic.annotation.Pattern;

import java.util.regex.Matcher;

/**
 * com.swj.framework.validation
 *
 * @Description 正则表达式校验器
 * @Author 余焕【yuh@3vjia.com】
 * @Datetime 2018/4/24 10:12
 **/
public class PatternValidator implements Validator {

    @Override
    public ValidateResult validate(String fieldName, Object entity, Object annotation) {
        Pattern pattern = (Pattern) annotation;

        if (entity == null) {
            return new ValidateResult(pattern.errorMessage());
        }

        Matcher matcher = java.util.regex.Pattern.compile(pattern.regexp()).matcher((String) entity);
        if (!matcher.matches()) {
            return new ValidateResult(pattern.errorMessage());
        }

        return new ValidateResult();
    }
}
