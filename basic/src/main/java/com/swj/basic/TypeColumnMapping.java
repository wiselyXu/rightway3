package com.swj.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * com.swj.framework.dao
 *
 * @DESCRIPTION 储存实体中的数据库字段
 * @AUTHOR Chenjw
 * @DATE 2018/3/13
 * @EMAIL chenjw@3vjia.com
 **/
public class TypeColumnMapping {

    private String tableName;

    private List<TableColumn> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }


    public TableColumn getColumn(String columnName)
    {
        for(TableColumn column : columns)
        {
            if(column.matchColumnName(columnName))
            {
                return column;
            }
        }
        return null;
    }

    public void addColumns(TableColumn column) {
        columns.add(column);
    }

    public TypeColumnMapping()
    {
        columns=new ArrayList<>();
    }
}
