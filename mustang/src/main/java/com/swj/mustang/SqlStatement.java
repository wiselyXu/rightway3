package com.swj.mustang;


import com.swj.basic.helper.StringHelper;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 刘海峰【liuhf@3vjia.com】
 * @since 2018/3/9-11:47
 **/
public class SqlStatement {

    private List<SqlClause> Clauses;

    public SqlStatement() {
    }

    public SqlStatement(List<SqlClause> clauses) {
        Clauses = clauses;
    }

    public List<SqlClause> getClauses() {
        return Clauses;
    }

    public void setClauses(List<SqlClause> clauses) {
        Clauses = clauses;
    }


    public String getStatement(Map<String, Object> parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Clauses.size(); i++) {
            SqlClause clause = Clauses.get(i);
            if (StringHelper.isNullOrEmpty(clause.getParameter())) {
                stringBuilder.append(" " + clause.getClause());
            } else if (parameter != null) {
                for (String k : parameter.keySet()) {
                    if (k.toLowerCase().equals(clause.getParameter().toLowerCase())) {
                        // in子句
                        if (clause.isInCause()) {
                            String inClause = getInCause(parameter.get(k));
                            if (!StringHelper.isObjectNullOrEmpty(inClause)) {
                                stringBuilder.append(" " + StringHelper.replaceIgnoreCase(clause.getClause(), "$" + k + "$", inClause));
                            }
                            break;
                        }

                        // like子句
                        if (clause.isLikeCause()) {
                            Object likeClause = parameter.get(k);
                            if (!StringHelper.isObjectNullOrEmpty(likeClause)) {
                                stringBuilder.append(" " + StringHelper.replaceIgnoreCase(clause.getClause(), "$" + k + "$", likeClause.toString()));
                            }
                            break;
                        }

                        // ??规则
                        if (clause.isAppendIfKeyExist()) {
                            if (parameter.get(k) == null) {
                                // is null
                                stringBuilder.append(StringHelper.replaceIgnoreCase(clause.getClause().replace("=", " is "), "#" + k + "#", "NULL"));
                                break;
                            }
                        } else { // ?规则
                            if (StringHelper.isObjectNullOrEmpty(parameter.get(k))) {
                                break;
                            }
                        }
                        stringBuilder.append(" " + clause.getClause());
                        break;
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 拼接SQL IN子句的参数值
     **/
    private String getInCause(Object parameter) {
        String result = "";

        if (parameter == null) return "";

        // 数组类型
        if (parameter.getClass().isArray()) {
            int length = Array.getLength(parameter);

            if (length == 0) return "";

            for (int i = 0; i < length; i++) {
                if (Array.get(parameter, i) == null) return "";
                result = result + "'" + String.valueOf(Array.get(parameter, i)) + "',";
            }
            return result.substring(0, result.length() - 1);
        }
        // 集合类型
        else if (parameter instanceof Collection) {
            Collection collection = (Collection) parameter;
            for (Object object : collection) {
                if (object == null) return "";
                result = result + "'" + object.toString() + "',";
            }
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (SqlClause clause : Clauses) {
            if (clause.getParameter().equals("")) {
                stringBuilder.append(String.format("%s\n", clause.getClause()));
            } else {
                stringBuilder.append(String.format("{? %s }\n", clause.getClause()));
            }
        }
        return stringBuilder.toString();
    }


}
