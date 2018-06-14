package com.swj.basic;



import com.swj.basic.helper.ObjectHelper;

import java.lang.reflect.Field;
import java.text.ParseException;

/**
 * com.swj.dao.model
 *
 * @Description
 * @Author 刘海峰【liuhf@3vjia.com】
 * @Datetime 2017/11/28-9:37
 **/
public class TableColumn {
    /**
     * 表字段
     */
    private String name;
    /**
     * 是否是插入字段
     */
    private boolean isInsert;
    /**
     * 是否更新字段
     */
    private boolean isUpdate;
    /**
     * 是否查询字段
     */
    private boolean isSelect;
    /**
     * 是否主键
     */
    private boolean isPK;

    /**
    *是否自增列
    */
    private boolean isAutoIncrement;

    /**
     * 描述

     */
    private String description;
    /**
     * 变量名
     */
    private String fieldName ;

    private Field field;

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public void setValue(Object instance, Object value) throws ParseException {
        try {
            if(this.field!=null && value!=null) {
                Object temp= ObjectHelper.convertToType(field.getType(), value);
                if(temp!=null)
                {
                    this.field.set(instance, temp);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean matchColumnName(String resultSetName)
    {
        String tempName=resultSetName.toLowerCase();
        if(tempName.equals(name.toLowerCase()) || tempName.equals(fieldName.toLowerCase()))
        {
            return true;
        }
        tempName = tempName.replaceAll("_","");
        if(tempName.equals(name.toLowerCase()) || tempName.equals(fieldName.toLowerCase()))
        {
            return true;
        }
        return false;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInsert() {
        return isInsert;
    }

    public void setInsert(boolean insert) {
        isInsert = insert;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isPK() {
        return isPK;
    }

    public void setPK(boolean PK) {
        isPK = PK;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
