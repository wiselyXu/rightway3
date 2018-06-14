package com.swj.basic;

import java.io.Serializable;
import java.util.List;


public class PageResult<T> implements Serializable {

    private int recordCount;

    private List<T> result;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
