package com.swj.mustang;


import com.swj.basic.TableColumn;

public class PropertyMapping {

    private TableColumn tableColumn;

    private int resultSetIndex;

    public TableColumn getTableColumn() {
        return tableColumn;
    }

    public void setTableColumn(TableColumn tableColumn) {
        this.tableColumn = tableColumn;
    }

    public int getResultSetIndex() {
        return resultSetIndex;
    }

    public void setResultSetIndex(int resultSetIndex) {
        this.resultSetIndex = resultSetIndex;
    }
}
