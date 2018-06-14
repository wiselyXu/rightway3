package com.swj.basic;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * returnList
 * @author  Chenjw
 * @since  2018/3/26
 **/

public class ReturnList<T> implements Serializable{
    @JSONField(name = "ReturnList")
    private List<T> returnList;

    @JSONField(name = "TotalResults")
    private int totalResults ;

    public List<T> getReturnList() {
        return returnList;
    }

    public void setReturnList(List<T> returnList) {
        this.returnList = returnList;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public ReturnList() {
    }

    public ReturnList(List<T> returnList) {
        this.returnList = returnList;
        this.totalResults = returnList.size();
    }

    public ReturnList(List<T> returnList, int totalResults) {
        this.returnList = returnList;
        this.totalResults = totalResults;
    }

    public ReturnList(PageResult<T> pageResult)
    {
        this.returnList=pageResult.getResult();
        this.totalResults=pageResult.getRecordCount();
    }
}
