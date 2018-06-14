package com.swj.mustang;


public class SqlClause
{
    /**
     * sql分句
     * */
    private String clause;

    /**
     * 分句中包含的参数名
     * */
    private String parameter;

    /**
     * 指示是否参数名存在则附加，为true时，不判断parameter中对应key的值为空或为null
     * */
    private boolean appendIfKeyExist;

    /**
     * 指示当前分句是否为in子句
     * */
    private boolean isInCause;

    /**
     * 指示当前分句是否为like子句
     * */
    private boolean isLikeCause;


    public boolean isInCause() {
        return isInCause;
    }

    public boolean isLikeCause() {
        return isLikeCause;
    }

    public void setLikeCause(boolean likeCause) {
        isLikeCause = likeCause;
    }

    public void setInCause(boolean inCause) {
        isInCause = inCause;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public boolean isAppendIfKeyExist() {
        return appendIfKeyExist;
    }

    public void setAppendIfKeyExist(boolean appendIfKeyExist) {
        this.appendIfKeyExist = appendIfKeyExist;
    }
}
