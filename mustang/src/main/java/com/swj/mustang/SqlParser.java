package com.swj.mustang;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql语句解析器
 * 1.把xml文件中的动态sql语句按{? ..}或{?? ..}分离成一个个sql分句
 * 2.识别出分句中包含在#..#或$..$中的参数
 * @author liuhf
 * @since 2018/3/14
 **/
public class SqlParser {

    private static final String paramPattern = "#[a-z|A-Z|0-9|_]+#";

    private static final String paramPatternDoller = "\\$[a-z|A-Z|0-9|_]+\\$";

    public static List<SqlClause> parse(String text) throws Exception {
        List<SqlClause> clauses = new ArrayList<>();
        String[] textSplit = text.split("\\{|\\}");
        boolean isValid=false;
        for (int i = 0; i < textSplit.length; i++) {
            String sentence = textSplit[i].trim();
            if (sentence.length() > 0) {
                isValid=false;
                SqlClause clause = new SqlClause();
                clause.setParameter("");
                if (sentence.startsWith("??")) {
                    sentence = sentence.substring(2);
                    clause.setAppendIfKeyExist(true);
                    isValid=true;
                }
                else if (sentence.startsWith("?")) {
                    sentence = sentence.substring(1);
                    clause.setAppendIfKeyExist(false);
                    isValid=true;
                }
                if(isValid) {
                    Matcher paramMatcher = Pattern.compile(paramPattern).matcher(sentence);
                    Matcher paramMatcherDollar = Pattern.compile(paramPatternDoller).matcher(sentence);
                    if (paramMatcher.find()) {
                        clause.setParameter(paramMatcher.group(0).replace("#", ""));
                    } else if (paramMatcherDollar.find()) {
                        if (sentence.toUpperCase().contains(" IN ")) {
                            clause.setInCause(true);
                        }
                        if(sentence.toUpperCase().contains(" LIKE "))
                        {
                            clause.setLikeCause(true);
                        }
                        if(clause.isInCause() && clause.isLikeCause())
                        {
                            throw new IllegalSqlStatementException("动态表达式$……$不能同时用于in或like子句");
                        }
                        if(!clause.isInCause() && !clause.isLikeCause())
                        {
                            throw new IllegalSqlStatementException("动态表达式$……$只能用于in或like子句");
                        }
                        clause.setParameter(paramMatcherDollar.group(0).replace("$", ""));
                    } else {
                        throw new IllegalSqlStatementException("动态表达式中必须包含动态参数名");
                    }
                }
                clause.setClause(sentence);
                clauses.add(clause);
            }
        }
        return clauses;
    }

}

